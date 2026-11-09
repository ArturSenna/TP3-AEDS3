# Ajuda Aí 1.0 — TP3 AEDS III

Sistema de perguntas e respostas inspirado no StackOverflow, desenvolvido como terceiro trabalho prático da disciplina de Algoritmos e Estruturas de Dados III — PUC Minas.

---

## Participantes

| Nome                       | GitHub     |
| -------------------------- | ---------- |
| Arthur De Pinho De Almeida | imartzz    |
| Rafael Cardoso Machado     | icm3333    |
| Pedro Henrique Debs Rabelo |            |
| Artur Otoni Senna Luz      | ArturSenna |

---

## Vídeo de demonstração

> 📹 **Vídeo de Demonstração (YouTube):** _(link a ser adicionado)_

---

## Sobre o sistema (Novidades do TP3)

O TP3 parte do sistema entregue no TP2 (usuários, perguntas, respostas e avaliações sobre arquivos binários com Hash Extensível e Árvores B+) e acrescenta a **busca de perguntas por palavras-chave** usando um **índice invertido** com classificação **TF×IDF**:

1. **Índice invertido de palavras-chave (`ListaInvertida`)**: cada termo presente no atributo `palavrasChave` das perguntas passa a ter uma lista de pares `(idPergunta, TF)` armazenada em disco, usando a classe `ListaInvertida` disponibilizada pelo professor (arquivos `dados/perguntas/indicePalavrasChave.dicionario.db` e `indicePalavrasChave.blocos.db`).
2. **Processamento de termos (`ProcessadorTermos`)**: as palavras-chave e os termos digitados na busca passam pelo mesmo tratamento — minúsculas, sem acentos, separação por `;`, espaços e pontuação, descarte de numerais e de _stop words_ (lista em português adaptada ao domínio de um fórum de perguntas).
3. **Manutenção automática do índice**: o índice é atualizado sempre que uma pergunta é **incluída**, tem suas **palavras-chave alteradas**, é **arquivada** ou **excluída**. O contador de entidades usado no IDF acompanha essas operações.
4. **Busca por palavras-chave com TF×IDF**: a opção `(B) Busca por palavras-chave` do menu _Buscar perguntas_ (que no TP2 era um placeholder) recupera a lista de cada termo da consulta, multiplica o TF armazenado pelo IDF do termo (`log10(N / nº de perguntas com o termo)`), soma os valores por pergunta e apresenta os resultados **ordenados por relevância**, paginados de 5 em 5, exibindo a relevância calculada de cada pergunta.
5. **Integração com as telas do TP2**: a partir dos resultados da busca é possível abrir a pergunta, ler as respostas (ordenadas por nota), responder e avaliar, exatamente como na listagem de perguntas mais recentes.
6. **Script de validação (`TesteIndiceInvertido`)**: reproduz o exemplo dos 4 livros do enunciado e confere, automaticamente, as listas invertidas geradas e a ordem/valores da busca "Inteligência Artificial" (`[1, 3, 2]` com `0.142`, `0.110` e `0.042`).

---

## Como compilar e executar

```bash
# Na raiz do projeto
javac -encoding UTF-8 aed3/*.java entidades/*.java controle/*.java visao/*.java Principal.java PovoarDados.java TesteIndiceInvertido.java

# Sistema
java Principal

# (opcional) Recria a pasta ./dados com 2 usuários, 10 perguntas, 4 respostas e 1 avaliação para demonstração
java PovoarDados

# (opcional) Recria a pasta ./dados com o exemplo dos 4 livros do enunciado e confere listas e TF x IDF
java TesteIndiceInvertido
```

Usuários criados pelo `PovoarDados`: `arthur@puc.br` e `rafael@puc.br` (senha `1234`). Sugestões de busca: `índice em disco`, `arquivo`, `compressao`, `a pergunta sobre o hash`.

> Atenção: `PovoarDados` e `TesteIndiceInvertido` apagam a pasta `./dados` antes de começar.

---

## Estrutura de classes

### Pacote `aed3/` — Estruturas de dados base

| Classe                             | Responsabilidade                                                                                                                                    |
| ---------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------- |
| `Arquivo<T>`                       | CRUD genérico sobre arquivo binário com cabeçalho de registros e lista de excluídos (`primeiroLivre`)                                               |
| `HashExtensivel`                   | Tabela Hash Extensível para índice direto de usuários por email                                                                                     |
| `ArvoreBMais`                      | Árvore B+ para índices secundários e relacionamentos 1:N / N:N                                                                                      |
| `ListaInvertida` **(novo no TP3)** | Lista invertida do professor: dicionário `termo → endereço` + blocos encadeados de pares `(id, frequência)`, com contador de entidades no cabeçalho |
| `ElementoLista` **(novo no TP3)**  | Par `(id, frequência)` armazenado nos blocos da `ListaInvertida`                                                                                    |
| `ParIDEndereco`                    | Par (ID → endereço no arquivo)                                                                                                                      |
| `ParIdId`                          | Par (ID1, ID2) usado para vincular chaves nas Árvores B+                                                                                            |
| `ParNomeId`                        | Par (String, ID)                                                                                                                                    |
| `InterfaceRegistro`                | Interface para entidades persistíveis no CRUD genérico                                                                                              |
| `InterfaceArvoreBMais`             | Interface para elementos armazenáveis na Árvore B+                                                                                                  |
| `InterfaceHashExtensivel`          | Interface para elementos armazenáveis no Hash Extensível                                                                                            |

### Pacote `entidades/` — Entidades e CRUDs especializados

| Classe                                  | Responsabilidade                                                                                                                                                                                                      |
| --------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `Usuario` / `ArquivoUsuario`            | Entidade usuário e CRUD com índice Hash Extensível por email                                                                                                                                                          |
| `Pergunta`                              | Entidade pergunta: `id`, `idUsuario`, `criacao`, `alteracao`, `nota`, `pergunta`, `palavrasChave` (termos separados por `;`), `ativa`                                                                                 |
| `ArquivoPergunta` **(alterada no TP3)** | Estende `Arquivo<Pergunta>`; além das Árvores B+ de usuário e de data, mantém a `ListaInvertida` de palavras-chave (`create`, `update`, `arquivar`, `delete`) e implementa a busca `readByPalavrasChave()` com TF×IDF |
| `ProcessadorTermos` **(novo no TP3)**   | Normalização (minúsculas, sem acentos), separação em palavras e remoção de _stop words_ e numerais                                                                                                                    |
| `ResultadoBusca` **(novo no TP3)**      | Par `(Pergunta, relevância)` devolvido pela busca por palavras-chave                                                                                                                                                  |
| `ParDataId`                             | Elemento da Árvore B+ `(dataCriacao, idPergunta)` em ordem decrescente                                                                                                                                                |
| `Resposta` / `ArquivoResposta`          | Entidade resposta e CRUD com Árvores B+ `(idPergunta, idResposta)` e `(idUsuario, idResposta)`                                                                                                                        |
| `Avaliacao` / `ArquivoAvaliacao`        | Entidade avaliação e CRUD com Árvores B+ `(idUsuario, idAvaliacao)` e `(idResposta, idAvaliacao)`                                                                                                                     |
| `ParEmailId`                            | Par (hash do email → idUsuario) para o Hash Extensível                                                                                                                                                                |

### Pacote `controle/` — Lógica de negócio

| Classe                                   | Responsabilidade                                                                                                       |
| ---------------------------------------- | ---------------------------------------------------------------------------------------------------------------------- |
| `ControleUsuario`                        | Login, cadastro, recuperação de senha e alteração de dados do usuário                                                  |
| `ControlePergunta` **(alterada no TP3)** | Criação, listagem paginada por data, alteração, arquivamento e **busca por palavras-chave** (`buscarPorPalavrasChave`) |
| `ControleResposta`                       | Criação de respostas, listagem por pergunta/usuário, arquivamento e ajuste de notas                                    |
| `ControleAvaliacao`                      | Registro de votos, validação de duplicidade, estorno e cancelamento de avaliações                                      |

### Pacote `visao/` — Interface de terminal

| Classe                                       | Responsabilidade                                                                                                                                                  |
| -------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `VisaoLogin`                                 | Tela inicial: login, cadastro e recuperação de senha                                                                                                              |
| `VisaoMenuPrincipal`                         | Navegação principal: acesso à Minha Área e Busca de perguntas                                                                                                     |
| `VisaoBuscarPerguntas` **(alterada no TP3)** | Perguntas mais recentes e **busca por palavras-chave** (prompt de termos, resultados paginados com relevância TF×IDF, abertura da pergunta, resposta e avaliação) |
| `VisaoMinhaArea`                             | Menu da área pessoal do usuário                                                                                                                                   |
| `VisaoMeusDados`                             | Alteração de dados cadastrais                                                                                                                                     |
| `VisaoPerguntas`                             | Gestão de perguntas do usuário (incluir, alterar palavras-chave, arquivar) e moderação de respostas recebidas                                                     |
| `VisaoMinhasRespostas`                       | Visualização e arquivamento de respostas do autor                                                                                                                 |
| `VisaoMinhasAvaliacoes`                      | Visualização e cancelamento de votos com estorno de nota                                                                                                          |

### Raiz do projeto

| Classe                                   | Responsabilidade                                                                                                                  |
| ---------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------- |
| `Principal`                              | Ponto de entrada: cria as pastas de dados, os controles e as telas                                                                |
| `PovoarDados` **(alterada no TP3)**      | Recria a base de demonstração com 10 perguntas cujas palavras-chave exercitam TF (termos repetidos) e IDF (termos raros × comuns) |
| `TesteIndiceInvertido` **(novo no TP3)** | Reproduz o exemplo dos livros do enunciado e confere listas, busca, alteração, arquivamento e persistência                        |

---

## Operações especiais implementadas

### 1. Processamento dos termos (`ProcessadorTermos`)

Antes de entrar no índice (ou de ser usado numa consulta), todo texto passa por `extrairTermos()`:

1. **Normalização**: decomposição Unicode (`Normalizer.Form.NFD`), remoção das marcas de acentuação (`\p{M}`) e conversão para minúsculas — `"Introdução à Inteligência"` → `"introducao a inteligencia"`.
2. **Separação em palavras**: qualquer sequência que não seja letra ou dígito é separador. Assim o `;` que separa as palavras-chave, espaços e sinais como `-`, `+` e `/` são tratados da mesma forma: `"hash extensivel;arvore b+;tf-idf"` → `[hash, extensivel, arvore, b, tf, idf]`.
3. **Descarte de numerais** (`"3"`, `"2026"`) e de **stop words**.

A lista de _stop words_ (constante `STOP_WORDS`) reúne artigos, preposições, contrações, pronomes, conjunções, advérbios e verbos auxiliares do português e foi **adequada ao domínio** do sistema: palavras que aparecem em praticamente toda pergunta de um fórum e por isso não ajudam a distinguir uma pergunta da outra — `pergunta`, `duvida`, `ajuda`, `alguem`, `sabe`, `gostaria`, `preciso`, `favor`, `obrigado`, `dica`, `exemplo`, `funciona`, `usar`, `melhor`, `forma`, `jeito`, `coisa`, `tipo`, entre outras. Exemplo: `"a pergunta 3 sobre o Hash"` → `[hash]`.

O vetor devolvido **mantém as repetições**, pois elas são necessárias para o cálculo do TF.

### 2. Construção do índice invertido (`ArquivoPergunta` + `ListaInvertida`)

`ArquivoPergunta` passou a ter um terceiro índice, além das duas Árvores B+:

```java
// índice invertido: termo das palavras-chave -> lista de (idPergunta, TF)
private ListaInvertida indicePalavrasChave;

indicePalavrasChave = new ListaInvertida(
    4,
    "./dados/perguntas/indicePalavrasChave.dicionario.db",
    "./dados/perguntas/indicePalavrasChave.blocos.db"
);
```

Ao criar uma pergunta (`create`), depois de gravá-la e indexá-la nas Árvores B+, o método privado `indexarPalavrasChave()` insere o par `(idPergunta, TF)` na lista de cada termo. O TF é a taxa de ocorrência do termo entre as palavras válidas da pergunta: para `n` termos, cada ocorrência vale `1/n`; como a `ListaInvertida` não aceita o mesmo `(termo, id)` duas vezes, a segunda ocorrência de um termo recupera o valor atual com `read()` e acumula com `update()`, exatamente como descrito no enunciado:

```java
float frequencia = 1.0f / termos.size();
for (String termo : termos) {
    ElementoLista e = indicePalavrasChave.read(termo, p.getId());
    if (e == null) {
        indicePalavrasChave.create(termo, new ElementoLista(p.getId(), frequencia));
    } else {
        indicePalavrasChave.update(termo, new ElementoLista(p.getId(), frequencia + e.getFrequencia()));
    }
}
```

Por fim, `create` chama `indicePalavrasChave.incrementaEntidades()` para que o total de perguntas (o `N` do IDF) fique registrado no cabeçalho do dicionário.

Listas geradas pelo `PovoarDados` (saída de `ListaInvertida.print()`; `0.33333334` é `1/3` em `float`):

```
ENTIDADES: 10

LISTAS INVERTIDAS:
hash: (1;0.4)
extensivel: (1;0.2)
indice: (1;0.2) (2;0.25) (7;0.33333334)
disco: (1;0.2) (4;0.2)
arvore: (2;0.5)
b: (2;0.25)
crud: (3;0.25)
exclusao: (3;0.25)
arquivo: (3;0.25) (4;0.2) (5;0.25) (8;0.25) (9;0.2) (10;0.25)
registro: (3;0.25)
desempenho: (4;0.2)
entrada: (4;0.2)
saida: (4;0.2)
java: (5;0.25) (8;0.25)
serializacao: (5;0.25)
bytes: (5;0.25)
compressao: (6;0.33333334) (10;0.25)
lzw: (6;0.33333334)
dados: (6;0.33333334)
invertido: (7;0.16666667)
tf: (7;0.16666667)
idf: (7;0.16666667)
busca: (7;0.16666667)
randomaccessfile: (8;0.25)
leitura: (8;0.25)
ordenacao: (9;0.2)
externa: (9;0.2)
memoria: (9;0.2)
intercalacao: (9;0.2)
huffman: (10;0.25)
bits: (10;0.25)
```

Por exemplo, a pergunta 1 tem palavras-chave `hash extensivel;hash;indice;disco` → `[hash, extensivel, hash, indice, disco]` (5 termos): `hash` aparece 2 vezes e recebe TF `0.4`; os demais, `0.2`. A pergunta 4 tem `desempenho;disco;entrada e saida;arquivo`: o `e` é descartado como _stop word_ e sobram 5 termos com TF `0.2`.

### 3. Manutenção do índice em alteração, arquivamento e exclusão

O enunciado pede que as listas sejam atualizadas sempre que uma entidade for incluída, excluída ou alterada. Isso é feito dentro de `ArquivoPergunta`, de forma transparente para os controles e telas:

```java
@Override
public boolean update(Pergunta perguntaAtualizada) throws Exception {
    Pergunta perguntaAntiga = super.read(perguntaAtualizada.getId());
    if (perguntaAntiga == null) return false;
    if (!super.update(perguntaAtualizada)) return false;

    if (perguntaAntiga.isAtiva() && !perguntaAtualizada.isAtiva()) {
        // arquivamento: sai do índice e deixa de contar para o IDF
        desindexarPalavrasChave(perguntaAntiga);
        indicePalavrasChave.decrementaEntidades();
    } else if (!perguntaAntiga.isAtiva() && perguntaAtualizada.isAtiva()) {
        // reativação: volta para o índice
        indexarPalavrasChave(perguntaAtualizada);
        indicePalavrasChave.incrementaEntidades();
    } else if (perguntaAtualizada.isAtiva()
               && !perguntaAntiga.getPalavrasChave().equals(perguntaAtualizada.getPalavrasChave())) {
        // alteração das palavras-chave: remove os termos antigos e indexa os novos
        desindexarPalavrasChave(perguntaAntiga);
        indexarPalavrasChave(perguntaAtualizada);
    }
    return true;
}
```

- **Alteração** (`Minha área > Minhas perguntas > Alterar dados da pergunta`): se só o texto mudou, o índice fica intacto; se as palavras-chave mudaram, os termos antigos são removidos (`ListaInvertida.delete(termo, id)` para cada termo distinto) e os novos indexados.
- **Arquivamento** (exclusão lógica do TP2, `ativa = false`): `arquivar()` passa pelo mesmo `update()`, de modo que a pergunta arquivada **sai de todas as listas** e `decrementaEntidades()` é chamado. Decidimos retirá-la do índice (em vez de apenas filtrá-la na busca) para que o IDF considere somente perguntas ativas; a busca ainda confere `isAtiva()` por segurança.
- **Exclusão física** (`delete` / `deleteAllByUsuario`): além das Árvores B+, remove a pergunta das listas e decrementa o contador (se ainda estava ativa).

Na base de demonstração, alterar as palavras-chave da pergunta 1 para `hash extensivel;cestos;diretorio` esvazia as listas `indice`/`disco` dela e cria `cestos` e `diretorio`; arquivá-la em seguida esvazia `hash`, `extensivel`, `cestos` e `diretorio` e o contador cai de 10 para 9 (veja as capturas 6 e 7).

### 4. Busca com classificação TF×IDF (`ArquivoPergunta.readByPalavrasChave`)

```java
LinkedHashSet<String> termos = new LinkedHashSet<>(ProcessadorTermos.extrairTermos(consulta));
int totalEntidades = indicePalavrasChave.numeroEntidades();

HashMap<Integer, Float> relevancias = new HashMap<>();
for (String termo : termos) {
    ElementoLista[] lista = indicePalavrasChave.read(termo);
    if (lista.length == 0) continue;
    // IDF = log10(total de entidades / entidades que contêm o termo)
    float idf = (float) Math.log10((double) totalEntidades / lista.length);
    for (ElementoLista e : lista) {
        relevancias.merge(e.getId(), e.getFrequencia() * idf, Float::sum);
    }
}
// ... filtra perguntas ativas e ordena por relevância decrescente (empate: mais recente primeiro)
```

1. A consulta recebe o mesmo tratamento das palavras-chave; termos repetidos na consulta contam uma vez só.
2. `N` é lido do cabeçalho da lista invertida (`numeroEntidades()`); o tamanho de cada lista dá o número de perguntas que contêm o termo.
3. Cada `(id, TF)` da lista é multiplicado pelo IDF do termo e somado ao acumulador da pergunta (`HashMap<Integer, Float>`).
4. O resultado é devolvido como `ArrayList<ResultadoBusca>` ordenado pela relevância decrescente; em empate, a pergunta mais recente vem primeiro.

**Exemplo com a base do `PovoarDados`** — consulta `"índice em disco"` → termos `[indice, disco]`, `N = 10`:

| termo    | perguntas que contêm | IDF = log10(N / n)  |
| -------- | -------------------- | ------------------- |
| `indice` | 1, 2, 7 (n = 3)      | log10(10/3) = 0.523 |
| `disco`  | 1, 4 (n = 2)         | log10(10/2) = 0.699 |

| pergunta                        | TF(indice) × IDF      | TF(disco) × IDF     | relevância |
| ------------------------------- | --------------------- | ------------------- | ---------- |
| 1 — Hash Extensível             | 0.2 × 0.523 = 0.105   | 0.2 × 0.699 = 0.140 | **0.244**  |
| 7 — Índice invertido com TF-IDF | 0.333 × 0.523 = 0.174 | —                   | **0.174**  |
| 4 — Custo de I/O                | —                     | 0.2 × 0.699 = 0.140 | **0.140**  |
| 2 — Árvore B × B+               | 0.25 × 0.523 = 0.131  | —                   | **0.131**  |

Ordem apresentada: `[1, 7, 4, 2]` — exatamente o que a tela mostra (captura 2). A pergunta 1 lidera por conter os dois termos; a 7 supera a 2 porque `indice` aparece 2 vezes entre 6 termos (TF 0.333) contra 1 vez entre 4 (TF 0.25); a 4 supera a 2 porque `disco` é mais raro que `indice` (IDF maior).

Outros exemplos visíveis nas capturas: `"arquivo"` está em 6 das 10 perguntas (IDF `0.222`), gerando `0.055` para TF `0.25` e `0.044` para TF `0.2`, com empates resolvidos pela data; `"a pergunta sobre o hash"` vira apenas `[hash]` e devolve a pergunta 1 com `0.4 × log10(10/1) = 0.400`.

> A formatação decimal na tela (`0,244` ou `0.244`) segue o _locale_ do sistema.

### 5. Tela de busca por palavras-chave (`VisaoBuscarPerguntas`)

- `Buscar perguntas > (B) Busca por palavras-chave` pede os termos; ENTER vazio retorna. Se sobrar nenhum termo válido (só _stop words_/numerais) ou se nenhuma pergunta for encontrada, a tela avisa e pede novos termos.
- A tela de resultados mostra a consulta digitada, os **termos efetivamente considerados**, a paginação (5 por página, como na listagem por data) e, para cada pergunta, o texto, as palavras-chave cadastradas, a **relevância TF×IDF** e autor/data.
- Opções: `(A)`/`(B)` páginas, `(N)` nova busca, `(R)` retornar, ou o número da pergunta para abrir seus detalhes — a mesma tela de pergunta do TP2 (respostas ordenadas por nota, responder, concordar/discordar), com a trilha de navegação indicando a origem (`... > Busca por palavras-chave > Pergunta`).

### 6. Validação com o exemplo do enunciado (`TesteIndiceInvertido`)

O script cria 4 perguntas cujas palavras-chave são os títulos dos livros do enunciado e confere as listas e a busca. Saída real:

```
--- CONSTRUÇÃO DAS LISTAS (4 livros do enunciado como palavras-chave) ---
[OK]    numeroEntidades() == 4
[OK]    introducao: (1; 0.333) (4; 0.333)
[OK]    artificial: (1; 0.333) (3; 0.2)
[OK]    inteligencia: (1; 0.333) (2; 0.333) (3; 0.4)
[OK]    emocional: (2; 0.333)
[OK]    gestores: (2; 0.333)
[OK]    trabalho: (3; 0.2)
[OK]    meio: (3; 0.2)
[OK]    gestao: (4; 0.333)
[OK]    equipes: (4; 0.333)
[OK]    stop word "a" não foi indexada

LISTAS INVERTIDAS:
introducao: (1;0.33333334) (4;0.33333334)
inteligencia: (1;0.33333334) (2;0.33333334) (3;0.4)
artificial: (1;0.33333334) (3;0.2)
emocional: (2;0.33333334)
gestores: (2;0.33333334)
trabalho: (3;0.2)
meio: (3;0.2)
gestao: (4;0.33333334)
equipes: (4;0.33333334)

--- BUSCA "Inteligência Artificial" (esperado: [1, 3, 2] com 0.142, 0.110 e 0.042) ---
Resultado: (1; 0.142) (3; 0.110) (2; 0.042)
[OK]    ordem e valores TF x IDF
[OK]    termo repetido na consulta conta uma vez só
[OK]    consulta só com stop words -> nenhum resultado
[OK]    termo inexistente -> nenhum resultado

--- ALTERAÇÃO: livro 2 passa a ter palavras-chave "Gestão de Equipes Ágeis" ---
[OK]    inteligencia perde o id 2
[OK]    emocional fica vazia
[OK]    gestao ganha o id 2
[OK]    ageis: (2; 0.333)
[OK]    total de entidades continua 4
[OK]    alterar só o texto não mexe no índice

--- ARQUIVAMENTO: livro 1 é arquivado ---
[OK]    total de entidades cai para 3
[OK]    introducao: (4; 0.333)
[OK]    inteligencia: (3; 0.4)
Busca "inteligencia": (3; 0.191)
[OK]    só o livro 3 com 0.4 x log10(3/1) = 0.191
[OK]    outro usuário não consegue arquivar

--- PERSISTÊNCIA: fecha e reabre os arquivos ---
Busca "gestão de equipes": (4; 0.117) (2; 0.117)
[OK]    índice recuperado do disco

FALHAS: 0  (tudo conforme o enunciado)
```

---

## Capturas de tela

Telas copiadas de uma execução real sobre a base do `PovoarDados` (usuário `arthur@puc.br`). As capturas em imagem ficam em `docs/screenshots/`.

**1. Menu de busca e prompt de termos**

```
============================
      AJUDA AÍ 1.0
============================
> Início > Buscar perguntas
(A) Perguntas mais recentes
(B) Busca por palavras-chave
(R) Retornar
Opção: B

============================
      AJUDA AÍ 1.0
============================
> Início > Buscar perguntas > Busca por palavras-chave
Digite uma ou mais palavras (ENTER vazio para retornar).
Termos de busca: índice em disco
```

**2. Resultados classificados por TF×IDF** (acento removido, `em` descartado como _stop word_)

```
============================
      AJUDA AÍ 1.0
============================
> Início > Buscar perguntas > Busca por palavras-chave
Busca: "índice em disco"
Termos considerados: indice, disco
Página 1 de 1 (Total de perguntas encontradas: 4)

(1) Como funciona o Hash Extensivel em AEDS 3?
    Palavras-chave: hash extensivel;hash;indice;disco
    Relevância (TF x IDF): 0,244
    Criada por Arthur em 07/11/2026 às 15:20

(2) Como montar um indice invertido com TF-IDF?
    Palavras-chave: indice invertido;tf-idf;busca;indice
    Relevância (TF x IDF): 0,174
    Criada por Arthur em 07/11/2026 às 15:20

(3) Como calcular o custo de I/O em arquivos binarios?
    Palavras-chave: desempenho;disco;entrada e saida;arquivo
    Relevância (TF x IDF): 0,140
    Criada por Rafael em 07/11/2026 às 15:20

(4) Qual a diferenca entre Arvore B e Arvore B+?
    Palavras-chave: arvore b+;arvore;indice
    Relevância (TF x IDF): 0,131
    Criada por Rafael em 07/11/2026 às 15:20

(A) Página anterior
(B) Próxima página
(N) Nova busca
(R) Retornar
Ou digite o número da pergunta para abrir detalhes.
Opção: 1
```

**3. Pergunta aberta a partir da busca** (respostas por nota, responder e avaliar como no TP2)

```
============================
      AJUDA AÍ 1.0
============================
> Início > Buscar perguntas > Busca por palavras-chave > Pergunta

Como funciona o Hash Extensivel em AEDS 3?
Palavras-chave: hash extensivel;hash;indice;disco
Criada por Arthur em 07/11/2026 às 15:20

RESPOSTAS
---------
(1) O Hash Extensivel usa um diretorio em memoria/disco que aponta para cestos.
Respondido por Rafael em 07/11/2026 às 15:20
Nota: 1  (1 avaliações)

(2) Resposta redundante que pode ser moderada pelo autor.
Respondido por Rafael em 07/11/2026 às 15:20
Nota: 0  (0 avaliações)

(A) Responder à pergunta
(R) Retornar
Ou digite o número da resposta para interagir.
Opção: R
```

**4. Paginação dos resultados** (termo comum `arquivo`: 6 perguntas, 2 páginas, empates resolvidos pela data)

```
> Início > Buscar perguntas > Busca por palavras-chave
Busca: "arquivo"
Termos considerados: arquivo
Página 1 de 2 (Total de perguntas encontradas: 6)

(1) Como funciona a compactacao de um arquivo com Huffman?
    Palavras-chave: compressao;huffman;arquivo;bits
    Relevância (TF x IDF): 0,055
    Criada por Rafael em 07/11/2026 às 15:20

(2) Como abrir um arquivo binario em Java com RandomAccessFile?
    Palavras-chave: java;arquivo;randomaccessfile;leitura
    Relevância (TF x IDF): 0,055
    Criada por Rafael em 07/11/2026 às 15:20

(3) Melhores praticas para serializacao binaria em Java?
    Palavras-chave: java;serializacao;bytes;arquivo
    Relevância (TF x IDF): 0,055
    Criada por Arthur em 07/11/2026 às 15:20

(4) O que e a lista de registros excluidos (primeiroLivre)?
    Palavras-chave: crud;exclusao;arquivo;registro
    Relevância (TF x IDF): 0,055
    Criada por Arthur em 07/11/2026 às 15:20

(5) Qual a melhor forma de ordenar um arquivo grande que nao cabe em memoria?
    Palavras-chave: ordenacao externa;arquivo;memoria;intercalacao
    Relevância (TF x IDF): 0,044
    Criada por Arthur em 07/11/2026 às 15:20

(A) Página anterior
(B) Próxima página
(N) Nova busca
(R) Retornar
Ou digite o número da pergunta para abrir detalhes.
Opção: B

> Início > Buscar perguntas > Busca por palavras-chave
Busca: "arquivo"
Termos considerados: arquivo
Página 2 de 2 (Total de perguntas encontradas: 6)

(1) Como calcular o custo de I/O em arquivos binarios?
    Palavras-chave: desempenho;disco;entrada e saida;arquivo
    Relevância (TF x IDF): 0,044
    Criada por Rafael em 07/11/2026 às 15:20
```

**5. Stop words na consulta** (`"a pergunta sobre o hash"` → apenas `hash`)

```
> Início > Buscar perguntas > Busca por palavras-chave
Busca: "a pergunta sobre o hash"
Termos considerados: hash
Página 1 de 1 (Total de perguntas encontradas: 1)

(1) Como funciona o Hash Extensivel em AEDS 3?
    Palavras-chave: hash extensivel;hash;indice;disco
    Relevância (TF x IDF): 0,400
    Criada por Arthur em 07/11/2026 às 15:20
```

**6. Alteração das palavras-chave atualiza o índice**

```
> Início > Minha área > Minhas perguntas > Pergunta

07/11/2026 15:20
Como funciona o Hash Extensivel em AEDS 3?
Palavras chave: hash extensivel;hash;indice;disco
...
(A) Alterar dados da pergunta
(B) Arquivar pergunta
(R) Retornar
Opção: A
Novo texto (ENTER para manter):
Novas palavras-chave (ENTER para manter): hash extensivel;cestos;diretorio
Pergunta alterada com sucesso.
```

Repetindo a busca `"índice em disco"`, a pergunta 1 não aparece mais e as relevâncias das demais mudam, pois os IDFs foram recalculados (`indice` agora está em 2 das 10 perguntas → `log10(5) = 0,699`; `disco` em 1 → `log10(10) = 1`):

```
Busca: "índice em disco"
Termos considerados: indice, disco
Página 1 de 1 (Total de perguntas encontradas: 3)

(1) Como montar um indice invertido com TF-IDF?
    Palavras-chave: indice invertido;tf-idf;busca;indice
    Relevância (TF x IDF): 0,233
    Criada por Arthur em 07/11/2026 às 15:20

(2) Como calcular o custo de I/O em arquivos binarios?
    Palavras-chave: desempenho;disco;entrada e saida;arquivo
    Relevância (TF x IDF): 0,200
    Criada por Rafael em 07/11/2026 às 15:20

(3) Qual a diferenca entre Arvore B e Arvore B+?
    Palavras-chave: arvore b+;arvore;indice
    Relevância (TF x IDF): 0,175
    Criada por Rafael em 07/11/2026 às 15:20
```

E o novo termo já é encontrado:

```
Busca: "cestos"
Termos considerados: cestos
Página 1 de 1 (Total de perguntas encontradas: 1)

(1) Como funciona o Hash Extensivel em AEDS 3?
    Palavras-chave: hash extensivel;cestos;diretorio
    Relevância (TF x IDF): 0,250
    Criada por Arthur em 07/11/2026 às 15:20
```

**7. Arquivamento remove a pergunta do índice**

```
> Início > Minha área > Minhas perguntas > Pergunta
...
Opção: B
Confirma arquivamento definitivo da pergunta? (S/N): S
Pergunta arquivada.

> Início > Buscar perguntas > Busca por palavras-chave
Digite uma ou mais palavras (ENTER vazio para retornar).
Termos de busca: cestos
Termos considerados: cestos
Nenhuma pergunta encontrada para os termos informados.
```

---

## Checklist do enunciado

**1. O índice invertido com palavras-chave foi criado usando a classe ListaInvertida?**

> **Sim.** `ArquivoPergunta` mantém o atributo `indicePalavrasChave` do tipo `aed3.ListaInvertida` (código do professor, sem alterações), persistido em `dados/perguntas/indicePalavrasChave.dicionario.db` e `indicePalavrasChave.blocos.db`. Os termos vêm do atributo `palavrasChave` da pergunta após o tratamento do `ProcessadorTermos`, o TF é acumulado com `read()`/`create()`/`update()` e o total de entidades é mantido com `incrementaEntidades()`/`decrementaEntidades()`. O índice é atualizado na inclusão, na alteração das palavras-chave, no arquivamento e na exclusão.

**2. É possível buscar perguntas por palavras-chave no menu de buscar perguntas?**

> **Sim.** Em `Início > Buscar perguntas > (B) Busca por palavras-chave` (`VisaoBuscarPerguntas`), o usuário digita um ou mais termos; a tela mostra os termos considerados após a normalização, os resultados paginados com a relevância de cada pergunta e permite abrir a pergunta para ler/responder/avaliar.

**3. As perguntas retornadas estão corretamente classificadas pelo modelo TFxIDF?**

> **Sim.** `readByPalavrasChave()` multiplica o TF armazenado nas listas pelo IDF `log10(N / nº de perguntas com o termo)`, soma por pergunta e ordena em ordem decrescente, como descrito no enunciado. O `TesteIndiceInvertido` reproduz o exemplo dos 4 livros e obtém exatamente as listas do enunciado e a busca "Inteligência Artificial" → `[1, 3, 2]` com `0.142`, `0.110` e `0.042`; a seção _Operações especiais_ mostra o cálculo passo a passo para a base de demonstração, batendo com os valores exibidos na tela.

**4. O trabalho compila corretamente?**

> **Sim.** Compilado com `javac -encoding UTF-8` (JDK 25) em todos os pacotes (`aed3`, `entidades`, `controle`, `visao`), além de `Principal`, `PovoarDados` e `TesteIndiceInvertido`, sem erros.

**5. O trabalho está completo e funcionando sem erros de execução?**

> **Sim.** Todos os fluxos (busca com um e vários termos, _stop words_, acentos, paginação, abertura da pergunta a partir da busca, alteração de palavras-chave, arquivamento, persistência do índice entre execuções) foram exercitados manualmente e pelo `TesteIndiceInvertido`, sem erros de execução. As funcionalidades do TP2 continuam funcionando.

**6. O trabalho é original e não a cópia de um trabalho de outro grupo?**

> **Sim.** O código foi desenvolvido pelos membros do grupo a partir do nosso TP2, usando apenas as estruturas de dados disponibilizadas na disciplina (`Arquivo`, `HashExtensivel`, `ArvoreBMais`, `ListaInvertida`).
