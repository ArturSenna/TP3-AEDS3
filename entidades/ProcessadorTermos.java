package entidades;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// transforma textos (palavras-chave e consultas) no vetor de termos usado pelo índice invertido:
// minúsculas, sem acentos, sem pontuação, sem stop words e sem numerais
public class ProcessadorTermos {

    // palavras vazias (stop words) do português + termos genéricos do domínio de perguntas e respostas
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        // artigos
        "o", "a", "os", "as", "um", "uma", "uns", "umas",
        // preposições e contrações
        "de", "do", "da", "dos", "das", "em", "no", "na", "nos", "nas", "num", "numa",
        "por", "pelo", "pela", "pelos", "pelas", "para", "pra", "pro", "com", "sem",
        "sob", "sobre", "entre", "ate", "apos", "ante", "contra", "desde", "perante",
        "ao", "aos", "nele", "nela", "neles", "nelas", "nisso", "nisto", "naquele", "naquela",
        "deste", "desta", "destes", "destas", "desse", "dessa", "desses", "dessas",
        "daquele", "daquela", "disso", "disto", "daquilo",
        // pronomes
        "eu", "tu", "ele", "ela", "eles", "elas", "voce", "voces", "me", "te", "se", "lhe", "lhes",
        "meu", "minha", "meus", "minhas", "teu", "tua", "teus", "tuas", "seu", "sua", "seus", "suas",
        "nosso", "nossa", "nossos", "nossas", "este", "esta", "estes", "estas", "esse", "essa",
        "esses", "essas", "aquele", "aquela", "aqueles", "aquelas", "isto", "isso", "aquilo",
        "quem", "que", "qual", "quais", "cujo", "cuja", "cujos", "cujas", "algum", "alguma",
        "alguns", "algumas", "nenhum", "nenhuma", "todo", "toda", "todos", "todas", "outro",
        "outra", "outros", "outras", "mesmo", "mesma", "mesmos", "mesmas", "cada", "tal", "tais",
        // conjunções e advérbios comuns
        "e", "ou", "mas", "porem", "nem", "pois", "porque", "como", "quando", "enquanto",
        "embora", "entao", "logo", "assim", "tambem", "ja", "ainda", "so", "apenas", "nao",
        "sim", "muito", "muita", "muitos", "muitas", "pouco", "pouca", "poucos", "poucas",
        "mais", "menos", "bem", "mal", "aqui", "ali", "la", "ca", "onde", "aonde", "hoje",
        "ontem", "amanha", "sempre", "nunca", "agora", "depois", "antes", "talvez", "quanto",
        "quanta", "quantos", "quantas", "bastante", "demais", "tao", "tanto", "tanta",
        // verbos de ligação e auxiliares frequentes
        "ser", "sou", "es", "sao", "era", "eram", "foi", "foram", "sera", "serao", "seja",
        "sejam", "sendo", "sido", "estar", "estou", "estao", "estava", "estavam", "esteve",
        "estiver", "estando", "ter", "tenho", "tem", "tinha", "tinham", "teve", "tive",
        "tiver", "tendo", "tido", "ha", "haver", "havia", "fazer", "faco", "faz", "fez",
        "fazem", "fiz", "feito", "fazendo", "ir", "vou", "vai", "vao", "ido", "poder",
        "posso", "pode", "podem", "podia", "poderia", "dever", "devo", "deve", "devem",
        "deveria", "querer", "quero", "quer", "querem", "queria", "dar", "dou", "dao", "deu",
        "ficar", "fica", "ficam", "ficou",
        // termos genéricos do domínio (fórum de perguntas e respostas): aparecem em quase
        // toda pergunta e por isso não ajudam a distinguir uma pergunta da outra
        "pergunta", "perguntas", "resposta", "respostas", "duvida", "duvidas", "ajuda",
        "ajudar", "ajudem", "alguem", "sabe", "sabem", "saber", "gostaria", "preciso",
        "favor", "obrigado", "obrigada", "galera", "pessoal", "dica", "dicas", "coisa",
        "coisas", "algo", "tipo", "forma", "jeito", "maneira", "melhor", "melhores",
        "funciona", "usar", "uso", "exemplo", "exemplos"
    ));

    // minúsculas e sem acentos (ex.: "Introdução" -> "introducao")
    public static String normalizar(String texto) {
        String nfd = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return nfd.replaceAll("\\p{M}+", "").toLowerCase();
    }

    public static boolean ehStopWord(String termo) {
        return STOP_WORDS.contains(termo);
    }

    // gera o vetor de termos válidos (com repetições) a partir de um texto;
    // os separadores são o ";" das palavras-chave, espaços e qualquer pontuação
    public static ArrayList<String> extrairTermos(String texto) {
        ArrayList<String> termos = new ArrayList<>();
        if (texto == null) return termos;

        String[] tokens = normalizar(texto).split("[^a-z0-9]+");
        for (String token : tokens) {
            if (token.isEmpty())         continue; // separadores consecutivos
            if (token.matches("[0-9]+")) continue; // numerais são irrelevantes
            if (ehStopWord(token))       continue; // palavras vazias
            termos.add(token);
        }
        return termos;
    }
}
