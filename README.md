
### Jar Client ###

No caso, seria possível mudar um pouco a proposta?? no caso trabalhar com 2 micro-serviços, os papeis seriam os mesmos, havendo um client e um server, contudo as chamadas via linha de comando serão baseadas em requisições HTTP ao Jar Client, isso vai mais de acordo com a proposta de micro-serviço, além de fornecer uma maior flexibilidade caso se deseje por exemplo executar uma chamada remota. Pode ser?
O jar cliente já serve como uma interface facilitadora para o serviço HTTP no jar server. Quem for fazer chamada direta via HTTP, vai apontar direto para o jar server.

### Jar Client/Server ###

Você não passou versão de JVM , estou pressupondo a mais recente, 8.

### Jar Server ###

"2[XX] => concatenar no número retornado pela function, completar com dois dígitos 0 => 00.", Isso seria sempre sucesso? Como um  HTTP Status 200


A questão do timeout será definido um para cada um mesmo, ou será propagado pela requisição?
Cada um terá o seu.

Banco Postgres

"Assim que possível lhe passo como conectar em uma base teste que tenha essa function.", tranquilo com o que você me passou já da pra trabalhar tranquilamente.
 

Complementando a tabela de erros no jar server, pensei em mais esses itens:




Jar Client (starline_client.jar)

 

Funcionará com base em execuções via linha de comando, servindo basicamente para o envio de valores para o Jar Server (abaixo um descritivo do JSON a ser enviado no web-service), porém precisará ter alguns parâmetros que possibilitem uma configuração.

Para as flags de configuração, o jar deverá escrever um arquivo starline_client.config no mesmo diretório onde se encontra, porém com valores criptografados. Se possível, criptografar o arquivo por completo, ao invés de termos um arquivo com key e values.

Haverá uma flag administrativa que possibilitará a visualização dos parâmetros e valores (-show), porém essa opção solicitará uma senha para exibição, sendo esta: starline_[soma dos números da data, elevado à quarta potência] Ex: 27/04/2018 => 2+7+0+4+2+0+1+8 => starline_331776.

O exit code retornado para o prompt, deverá ser o número que o web-service do Jar Server irá retornar na response.

Detalhamento dos parâmetros:

    Configurações de parâmetros que criam um arquivo criptografado (neste ponto pode ser uma criptografia básica que permite criptografar e descriptografar, é apenas para não termos os valores de configuração explícitos no arquivo de configuração)

      -token [value] => configura um token para este cliente, exemplo de valor: b3b7ea55-f11e-4fe3-af0b-8564a1964b6b  => terá sempre essa mesma quantidade de caracteres

      -timeout [value] => qtd de segundos aguardando resposta do web-service no jar server

      -host [value] => ip para conexão ao servidor

      -port [value] => número da porta para conexão ao servidor

    Flags operacionais

      [obrigatório]

        -value [value] (Este valor será sempre uma string. Caso tenha somente um parâmetro, deixa opcional informar o -value), exemplo: java -jar starline_client.jar “1078”

      [opcional]

        -date [date] -time [time] => caso não sejam informados, deve ser passado a data atual do servidor, formato obrigatório: dd/mm/yyyy e hh24:mi:ss

        -help => exibe um help sobre os três parâmetros acima apenas (-value, -date, -time)

      [administrativo]

        -show  => exibe na tela os valores de parâmetros configurados, pedindo uma senha, conforme regra acima

 

JSON a ser consumido pelo web-service REST no jar server:

 

{

"token":"b3b7ea55-f11e-4fe3-af0b-8564a1964b6b",

"date":"27/04/2018",

"time":"16:40:45",

"value":"1078"

}

 

Jar Server (starline_server.jar)

 

Proverá um web-service REST, que irá consumir um JSON a ser recebido e interpretar os valores recebidos e chamar uma function em um banco PostgreSQL.

Também possuirá o mesmo mecanismo de configuração via parâmetros em arquivo criptografado. Arquivo de configuração starline_server.conf.

O retorno do web-service será um único valor numérico, conforme tabela abaixo.

 

Detalhamento dos parâmetros:

      -db_host [value] => host do banco postgresql

      -db_port [value] => porta do banco postgresql

      -db_user [value] => usuário do banco postgresql

      -db_pass [value] => senha do banco postgresql

      -db_timeout [value] => qtd de segundos aguardando resposta da function no banco postgresql

      -rest_port [value] => porta a ser publicado o serviço REST

    Flags operacionais

      [opcional]

        -svc => caso seja informado, irá executar como um serviço, disponibilizando o web-service nas configurações informadas

      [administrativo]

        -show => exibe os valores de parâmetros configurados na tela, pedindo uma senha, conforme regra acima

 

O banco utilizado será um PostgreSQL, faremos a conexão via JDBC.

Function a ser chamada (o primeiro parâmetro será fixo). Todos os parâmetros serão string e irá retornar um number. Assim que possível lhe passo como conectar em uma base teste que tenha essa function.

INSERT_MSG(SOURCE, TOKEN, MSG_DATE, MSG_TIME, VALUE);

                                Exemplo: INSERT_MSG(‘JAR’, ‘b3b7ea55-f11e-4fe3-af0b-8564a1964b6b’, ‘27/04/2018’, ‘16:40:45’, ‘1078’)
     Tabela de retorno do serviço, valores numéricos (caso você ache interessante colocarmos mais validações específicas aqui, me avisa que alinhamos).

100 => banco sem resposta (excedido o timeout de conexão)
101 => usuário e senha inválida do banco
102 => erro na execução da procedure
103 => excedido o tempo de timeout na execução da function no banco
104 => JSON com erro no token (formato diferente do esperado)
105 => JSON com data inválida
106 => JSON com hora inválida
107 => Erro genérico de parse no JSON
2[XX] => concatenar no número retornado pela function, completar com dois dígitos 0 => 00
