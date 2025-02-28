## 💻 Sobre o projeto

Essa aplicação é uma variação da que é construída no treinamento <a href="https://spring.academy/courses/building-a-batch-application-with-spring-batch">Building a Batch Application with Spring Batch</a>.

Surgiu da necessidade que tive de desenvolver um pipeline que ingerisse os dados dos arquivos .csv que eram gerados por diversos dispositivos, do tipo Logger, remotos. 

Uma ferramenta como o Airbyte poderia ser usada? Nesse caso, infelizmente não, porque não era possível compartilhar repositórios na rede, e nem obter credenciais de rede para a ferramenta.

Por que essa solução funcionou então? Porque quem executa a ação (a aplicação) é o próprio servidor aonde está a aplicação e os arquivos. csv. A aplicação executa seus passos, mas os resultados são guardados num banco de dados externo, e para isso eu tinha credenciais (de acesso ao banco de dados) que serviam. É um situação peculiar, aonde a pasta do repositório dos arquivos .csv não poderia ser compartilhada, mas a máquina estava na rede e poderia acessar um banco de dados.

Durante a adaptação, adicionei algumas coisas da framework que não foram ensinadas no treinamento, como conversores, métodos de acionamento, rotinas de limpeza, etc.

Ela pode melhorar muito, desejo aplicar algum padrão de projeto que permita que a aplicação possa ler arquivos .csv (ou até outros) com cabeçalhos diferentes, talvez o strategy me atenda.

Pensei em usar mais de um banco de dados, creio que não seja tão difícil, só não implementei porque ainda não estou convencido se isso é realmente útil.

---

## :question: Como funciona esse projeto?

### Funcionamento

A aplicação 'batch-0.0.1-SNAPSHOT.jar' é acionada via linha de comando (java -jar target/batch-0.0.1-SNAPSHOT.jar), que executa todo o processo ETL:
* Copia arquivos .csv para um repositório 'staging' (E)
* Lê os registros dos arquivos .csv, trata cada um (T)
* Grava na tabela 'leitura' do banco de dados (L)

Também faz o processo de limpeza dos repositórios de arquivos, tanto interno quanto externo.

Por ser executado via linha de comando, posso configurar uma tarefa no servidor aonde a aplicação está hospedada, e agendar a execução do serviço

A aplicação deve ficar no mesmo nível da pasta csvs, que seria a pasta aonde são gravados os arquivos. Eu penso em criar um arquivo (.properties) externo à aplicação, para que seja fácil alterar esse path, sem a necessidade de compilação.

Para o banco de dados, eu usei uma imagem docker, bem mais simples que instalar.
Aproveitei as credenciais que usava num banco qualquer, deixei gravado para orientação.

---

## ⚙️ Funcionalidades

- [x] Executa ETL;
- [ ] Consegue arquivos com cabeçalhos diferentes;
---

## 🛠 Tecnologias
- **[OpenJdk 17](https://openjdk.org/)**
- **[Spring Boot 3.35](https://spring.io/projects/spring-boot)**
- **[Maven](https://maven.apache.org)**
- **[postgresql](https://www.postgresql.org/)**
- **[Docker](https://www.docker.com/)**

---

## 📝 Licença

Projeto desenvolvido por [Welington](www.linkedin.com/in/welington-ada), baseado em exercício ministrado no laboratório do treinamento <a href="https://spring.academy/courses/building-a-batch-application-with-spring-batch">Building a Batch Application with Spring Batch</a>) 

---
