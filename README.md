# Calculadora de Notas Mínimas

## Devs
- **Jeferson Oliveira Moreira** – 212148  
- **Lucas Camargo Oliveira** - 222231

---

## User Story

**EU COMO** Aluno  
**PRECISO/QUERO** ter uma **“calculadora” de notas mínimas** para alcançar a média **7** em um curso  
**PARA** dizer, baseado no peso das tarefas do curso, o quão bem preciso ir em uma determinada atividade.  

Assim, consigo identificar **quais atividades priorizar** para alcançar maior performance na conclusão do curso.

---

## BDDs

### BDD1
- **Dado que** simular nota e recalcular  
- **Quando** simulo uma nota em uma atividade  
- **Então** a calculadora mostra a nota mínima necessária para média final **7**  
  - e indica o status **"alcançável"** ou **"inviável"**  
  - e as notas mínimas das demais atividades são recalculadas

---

### BDD2
- **Dado que** a nota mínima calculada é **maior que 10**  
- **Quando** simulo uma nota em uma atividade  
- **Então** o status exibido é **"inviável"**

---
