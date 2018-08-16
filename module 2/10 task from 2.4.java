import java.util.HashMap; 
import java.util.Map;

class Main {
  public static void main(String[] args) {
    String[] roles = {
        "Городничий","Аммос Федорович",
        "Артемий Филиппович",
        "Лука Лукич"};

    String[] textLines = {
        "Городничий: Я пригласил вас, господа, с тем, чтобы сообщить вам пренеприятное известие: к нам едет ревизор.",
        "Аммос Федорович: Как ревизор?",
        "Артемий Филиппович: Как ревизор?",
        "Городничий: Ревизор из Петербурга, инкогнито. И еще с секретным предписаньем.",
        "Аммос Федорович: Вот те на!",
        "Артемий Филиппович: Вот не было заботы, так подай!",
        "Лука Лукич: Господи боже! еще и с секретным предписаньем!"};
		
    groupByRole(roles, textLines);
  }

  public static String groupByRole(String[] roles, String[] textLines) {
    int counterSpeechs = 0;
    Map<String,StringBuilder> speechs = new HashMap<String,StringBuilder>();
    for (int i = 0; i < textLines.length; i++) {
      for (int j = 0; j < roles.length; j++) {
        if (textLines[i].startsWith(roles[j] + ":")) {
          int countDel = roles[j].length() + 1;
          counterSpeechs++;
          StringBuilder prefix = new StringBuilder().append(counterSpeechs).append(")"); 
          StringBuilder sample = prefix.append(textLines[i].substring(countDel));
          addSpeech(roles[j], sample, speechs);
        }
      }
    }
    return printSpeeches(speechs, roles);
  }

  public static String concatSpeeches(Map<String,StringBuilder> speechs, String[] roles) {
    StringBuilder result = new StringBuilder("");
    for (int i = 0; i < roles.length; i++) {
      result = result.append(speechs.get(roles[i])).append("\n");
    }
    return result.toString();
  }

  public static void addSpeech(String role, StringBuilder speech, Map<String,StringBuilder> speechs) {
    StringBuilder renewed;
    if (speechs.containsKey(role)) {
      renewed = (speechs.get(role)).append(speech).append("\n");
    } else {
      renewed = new StringBuilder(role).append(":\n").append(speech).append("\n"); 
    }
    speechs.put(role, renewed);
  }
}