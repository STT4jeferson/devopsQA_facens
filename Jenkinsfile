pipeline {
  agent any

  environment {
    MAVEN_CMD   = "./mvnw -Dmaven.repo.local=.m2/repository"
    QUALITY_OK  = "false"
    IMAGE_TAG   = "lab4/calculator:dev"
    TRIVY_IMAGE = "aquasec/trivy:0.57.1"
    TRIVY_CACHE = ".trivy-cache"
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
        sh 'chmod +x mvnw'
      }
    }

    stage('Trivy_FS_PreBuild') {
      steps {
        sh "mkdir -p ${TRIVY_CACHE}"
        sh """
          docker run --rm \
            -v ${WORKSPACE}:/workspace \
            -v ${WORKSPACE}/${TRIVY_CACHE}:/root/.cache \
            ${TRIVY_IMAGE} fs \
              --scanners vuln \
              --severity HIGH,CRITICAL \
              --exit-code 1 \
              --no-progress \
              --format table \
              --skip-dirs /workspace/target,/workspace/.m2,/workspace/.git,/workspace/bin,/workspace/_backup_single_module_20251103_213806 \
              -o /workspace/trivy-fs.txt \
              /workspace
        """
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
          // Lê o XML do JaCoCo gerado pelo maven-jacoco-plugin
          def jacocoXml = readFile 'target/site/jacoco/jacoco.xml'
          def root = new XmlSlurper().parseText(jacocoXml)

          // Procura o counter do tipo INSTRUCTION
          def instructionCounter = root.counter.find { it.@type == 'INSTRUCTION' }
          if (!instructionCounter) {
            error "Counter INSTRUCTION não encontrado no jacoco.xml"
          }

          BigDecimal covered = instructionCounter.@covered.toBigDecimal()
          BigDecimal missed  = instructionCounter.@missed.toBigDecimal()
          BigDecimal total   = covered + missed
          BigDecimal ratio   = (total > 0 ? covered / total : 0)

          // Formata só pra mensagem
          def coverageStr = ratio.setScale(4, BigDecimal.ROUND_HALF_UP).toString()

          // Atualiza a variável de ambiente usada nas próximas stages
          env.QUALITY_OK = (ratio >= 0.99).toString()

          if (env.QUALITY_OK != 'true') {
            error "Coverage below 99% (${coverageStr})"
          } else {
            echo "Quality gate OK: INSTRUCTION coverage = ${coverageStr} (>= 0.99)"
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

    stage('Trivy_Image') {
      when {
        expression { env.QUALITY_OK == 'true' }
      }
      steps {
        sh "mkdir -p ${TRIVY_CACHE}"
        sh """
          docker run --rm \
            -v ${WORKSPACE}/${TRIVY_CACHE}:/root/.cache \
            -v /var/run/docker.sock:/var/run/docker.sock \
            ${TRIVY_IMAGE} image \
              --exit-code 1 \
              --severity HIGH,CRITICAL \
              --no-progress \
              --format table \
              -o trivy-image.txt \
              ${IMAGE_TAG}
        """
      }
    }

    stage('Deploy_Staging') {
      when {
        expression { env.QUALITY_OK == 'true' }
      }
      steps {
        sh "docker-compose -f docker-compose.staging.yml down || true"
        sh "docker-compose -f docker-compose.staging.yml up -d --remove-orphans"
        sh """
          sleep 10 && \
          curl -sSf http://localhost:8081/swagger-ui.html > /dev/null || \
          (docker-compose -f docker-compose.staging.yml logs && exit 1)
        """
      }
    }
  }

  post {
    always {
      archiveArtifacts artifacts: 'target/site/jacoco/**,target/pmd.xml,trivy-fs.txt,trivy-image.txt', fingerprint: true
    }
    success {
      echo 'Pipeline concluído com aprovação de 99%+'
    }
  }
}
