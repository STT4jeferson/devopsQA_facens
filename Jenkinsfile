pipeline {
  agent any

  environment {
    MAVEN_CMD = "./mvnw -Dmaven.repo.local=.m2/repository"
    QUALITY_OK = "false"
    IMAGE_TAG = "lab4/calculator:dev"
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
        sh 'chmod +x mvnw'
      }
    }

    stage('Pre-Build') {
      steps {
        sh "${MAVEN_CMD} -DskipTests clean pmd:check"
      }
    }

    stage('Pipeline-test-dev') {
      steps {
        sh "${MAVEN_CMD} clean verify"
        junit 'target/surefire-reports/*.xml'
        jacoco execPattern: 'target/jacoco.exec'
        recordIssues(tools: [pmdParser(pattern: 'target/pmd.xml')])
      }
    }

    stage('QualityGate-99') {
      steps {
        script {
          def coverage = sh(returnStdout: true, script: "python - <<'PY'\nimport xml.etree.ElementTree as ET\nroot = ET.parse('target/site/jacoco/jacoco.xml').getroot()\nctr = root.find(\"counter[@type='INSTRUCTION']\")\ncovered = float(ctr.get('covered'))\nmissed = float(ctr.get('missed'))\nratio = covered / (covered + missed) if (covered + missed) else 0\nprint(f'{ratio:.4f}')\nPY").trim()
          env.QUALITY_OK = (coverage.toBigDecimal() >= 0.99).toString()
          if (env.QUALITY_OK != 'true') {
            error "Coverage below 99% (${coverage})"
          }
        }
      }
    }

    stage('Image_Docker') {
      when {
        expression { env.QUALITY_OK == 'true' }
      }
      steps {
        sh "docker build -t ${IMAGE_TAG} ."
      }
    }

    stage('Deploy_Staging') {
      when {
        expression { env.QUALITY_OK == 'true' }
      }
      steps {
        sh "docker-compose -f docker-compose.staging.yml down || true"
        sh "docker-compose -f docker-compose.staging.yml up -d --remove-orphans"
        sh "sleep 10 && curl -sSf http://localhost:8081/swagger-ui.html > /dev/null || (docker-compose -f docker-compose.staging.yml logs && exit 1)"
      }
    }
  }

  post {
    always {
      archiveArtifacts artifacts: 'target/site/jacoco/**,target/pmd.xml', fingerprint: true
    }
    success {
      echo 'Pipeline concluído com aprovação de 99%+'   
    }
  }
}
