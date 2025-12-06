Feature: Calculadora de notas

  Scenario: Simular nota e recalcular
    Given que simulo uma nota em uma atividade
    When eu simulo a nota
    Then a calculadora mostra a nota mínima necessária para média final 7 e indica o status "alcançável" ou "inviável"
    And as notas mínimas das demais atividades são recalculadas

  Scenario: Nota mínima maior que 10 torna status inviável
    Given que a nota mínima calculada é maior que 10
    When eu simulo uma nota em uma atividade
    Then o status exibido é "inviável"
