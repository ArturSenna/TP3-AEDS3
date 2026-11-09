Este trabalho deverá implementar as **buscas de perguntas por meio dos termos presentes no atributo palavras-chave das perguntas**.

#### 

#### ÍNDICE INVERTIDO

O índice invertido nos permite fazer buscas por entidades a partir de seus termos (palavras). Para isso, a gente precisa criar uma lista de IDs para cada termo indexado. Por exemplo, suponha que os seguintes livros estejam cadastrados em um sistema de livros:

| ID | Nome |
| ----- | ----- |
| 1 | Introdução à Inteligência Artificial |
| 2 | Inteligência Emocional para Gestores |
| 3 | Inteligência no Trabalho por Meio da Inteligência Artificial  |
| 4 | Introdução à Gestão de Equipes |

A criação das listas começa com a inserção do primeiro livro. Ao criá-lo, as palavras do título (ou outro atributo) devem ser transformadas em um vetor de palavras. Em seguida, é necessário aplicar uma transformação para que as palavras restantes fiquem em letras minúsculas e sem acentos. Por exemplo, para o primeiro livro, o seguinte vetor será gerado: \[ "introducao", "a", "inteligencia", "artificial" \]. Agora, as palavras irrelevantes para as buscas, como os artigos, as preposições e os numerais devem ser descartados desse vetor. Essas palavras irrelevantes são chamadas de *stop words* (palavras vazias). Vocês podem encontrar listas de *stop words* em português na Internet, mas deve adequar essa lista para o domínio específico do projeto. O vetor final será: \[ "introducao", "inteligencia", "artificial" \].

Empregando a lógica do valor [TFxIDF](https://pt.wikipedia.org/wiki/Tf%E2%80%93idf)

[Links to an external site.](https://pt.wikipedia.org/wiki/Tf%E2%80%93idf)

 (*Term Frequency — Inverse Document Frequency*), o próximo passo é determinar a frequência do termo no nome do livro e qual o inverso da frequência do termo entre os livros. O valor TF indica a taxa de ocorrências de um determinado termo no título. Por exemplo, para o primeiro livro, o termo "introducao" aparece uma vez de um total de três termos (33.3% \= 0.333). Já para o terceiro livro, o termo "inteligencia" aparece 2 vezes em cinco palavras (após descartar as *stop words*) e sua frequência é, portanto, 0.4 (40%).

Já o IDF indica o inverso da taxa de ocorrências de um termo entre os títulos (ou entre as entidades). Por exemplo, o termo "inteligencia" aparece em 3 dos 4 livros. Como buscamos o inverso dessa taxa, o IDF desse termo será 4/3 \= 1.333. No entanto, para equilibrarmos os resultados, aplicaremos a função logarítmo (base 10\) a esse valor. Assim, o IDF do termo será efetivamente: log(4/3) \= 0.125

Após a inserção de todos os livros, chegaremos às seguintes listas invertidas:

\- introducao: (1; 0.333), (4; 0.333)

\- artificial: (1; 0.333), (3; 0.2)

\- inteligencia: (1; 0.333), (2; 0.333), (3; 0.4)

\- emocional: (2; 0.333)

\- gestores: (2; 0.333)

\- trabalho: (3; 0.2)

\- meio: (3; 0.2)

\- gestao: (4; 0.333)

\- equipes: (4; 0.333)

Note que as frequências foram registradas como uma taxa das palavras válidas (que não são *stop words*). Nessas listas não aparecem os valores de IDF, mas apenas os valores de TF. Mas sabemos que temos 4 livros (cujos dados devem estar armazenados em algum outro arquivo) e sabemos o tamanho de cada lista. Precisaremos dessas duas informações para o cálculo do IDF.

**Atenção:** Na classe ListaInvertida passada para vocês, o código não permite que o par ID, frequência seja cadastrado mais de uma vez para o mesmo livro. Então, é importante que vocês testem se o termo já existe e, nesse caso, recuperem o valor atual para acrescentar o novo valor a ele. O código ficará mais ou menos assim:

ElementoLista e \= lista.read(termo, id);

if(e==null) {

   lista.create(termo, new ElementoLista(id, frequencia))

} else {

   lista.update(termo, new ElementoLista(id, frequencia \+ e.getFrequencia()));

}

Por exemplo, no caso do livro 3, o vetor de termos será: \[ "inteligencia", "trabalho", "meio", "inteligencia", "artificial" \]. 5 palavras, cada uma com frequencia 0.2. Quando formos cadastrar o par (3; 0.2) para a primeira ocorrência do termo "inteligencia", usaremos o método create(), pois o valor retornado pelo método read() será null. Porém, quando chegarmos à segunda ocorrência do termo "inteligencia", o método read() nos retornará o par (3; 0.2). Somaremos a nova frequência a esse valor, totalizando 0.4, e usaremos o método update() para atualizar a frequência na lista. Além disso, não se esqueça de registrar todas as entidades cadastradas, para que possamos calcular o IDF. Neste exemplo, o total de entidades é 4\. A cada entidade, use o método incrementaEntidades().

Na hora da consulta, precisamos pedir ao usuário que digite os termos de busca. Por exemplo, suponha que ele digita: "Inteligência Artificial". Quando fizermos o mesmo tratamento que fizemos com os nomes dos livros, chegaremos ao vetor \[ "inteligencia", "artificial" \]. Em seguida, recuperamos a lista de cada termo e multiplicamos os valores de TF pelos valores de IDF dos termos. O IDF do termo "inteligencia" é 0.125, calculado por meio da função log(4/3) e do termo "artificial" é 0.301, calculado por meio de log(4/2). Assim, os valores recuperados já multiplicados pelos IDFs serão:

\- inteligencia: \[ (1; 0.042), (2; 0.042), (3; 0.050) \]

\- artificial: \[ (1; 0.100), (3; 0.060) \] 

Finalmente, geramos uma única lista com os valores que tenham o mesmo ID somados:

\[ (1; 0.142), (2; 0.042), (3; 0.110) \]

Ao ordenarmos essa lista pelo valor de cada ID, teremos a seguinte lista: \[1, 3, 2\]. O livro de ID 4 não aparece na lista, porque não tem nenhum dos termos da busca. Assim, os livros serão apresentados nessa ordem para os usuários. Em um sistema real, geralmente as respostas são apresentadas de 10 em 10 (como no Google).

Neste projeto, vocês devem implementar o índice invertido e a busca exatamente como descrito acima. Lembre-se de que o índice invertido usará as [listas invertidas cujo código está disponível](https://github.com/kutova/AEDsIII/tree/main/ListaInvertida)

[Links to an external site.](https://github.com/kutova/AEDsIII/tree/main/ListaInvertida)

. Nesse exemplo, elas devem ser atualizadas sempre que uma entidade for incluída, excluída ou tiver seus dados alterados.

#### 

#### O QUE DEVE SER FEITO?

* Implementar o [índice invertido](https://github.com/kutova/AEDsIII/tree/main/ListaInvertida)  
* [Links to an external site.](https://github.com/kutova/AEDsIII/tree/main/ListaInvertida)  
*  (construção das listas) para as perguntas usando as palavras-chave cadastradas nas perguntas.  
* Implementar a busca de perguntas por palavras oferecendo respostas ordenadas pelo valor TFxIDF.

Quanto tudo estiver pronto, as buscas por perguntas também deverão ser por meio das palavras-chave cadastradas nas perguntas.

#### 

#### FORMA DE ENTREGA

* **Código** \- Vocês devem postar o seu trabalho no GitHub e enviar apenas o URL do seu projeto. Criem um repositório específico para este projeto (ao invés de mandar o repositório pessoal de algum de vocês em que estejam todos os seus códigos). Acrescentem um arquivo **readme.md** ao projeto que será o relatório do trabalho de vocês (explicado abaixo).  
* **Relatório** \- O relatório deve começar com a **lista dos participantes** do trabalho prático e, em seguida, ter uma **descrição completa** do que o sistema faz. Capturem algumas telas e citem os nomes das classes que foram criadas. Expliquem todas as operações especiais que foram implementadas. O objetivo é que vocês facilitem ao máximo a minha correção, de tal forma que eu possa entender com facilidade tudo aquilo que fizeram e dar uma nota justa. No relatório, vocês devem, necessariamente, responder ao seguinte ***checklist*** (copie as perguntas abaixo para o seu relatório e responda sim/não em frente a elas, justificando a resposta quando necessário):  
  * O índice invertido com palavras-chave foi criado usando a classe ListaInvertida?  
  * É possível buscar perguntas por palavras-chave no menu de buscar perguntas?  
  * As perguntas retornadas estão corretamente classificadas pelo modelo TFxIDF?  
  * O trabalho compila corretamente?  
  * O trabalho está completo e funcionando sem erros de execução?  
  * O trabalho é original e não a cópia de um trabalho de outro grupo?

Lembre-se de que, para essa atividade, eu avaliarei tanto o esforço quanto o resultado. Portanto, escrevam o relatório e gravem o vídeo de forma que me ajude a observar o resultado.

**J**ustifiquem as respostas ao *checklist*.