enum Label {
  SPAM, NEGATIVE_TEXT, TOO_LONG, OK
}

interface TextAnalyzer {
  Label processText(String text);
}

class Main {
  public static void main(String[] args) {
    // initialization of analyzers for checking in the order of the given set of
    // analyzers
    String[] spamKeywords = { "spam", "bad" };
    int commentMaxLength = 40;
    TextAnalyzer[] textAnalyzers1 = { new SpamAnalyzer(spamKeywords), new NegativeTextAnalyzer(),
        new TooLongTextAnalyzer(commentMaxLength) };
    TextAnalyzer[] textAnalyzers2 = { new SpamAnalyzer(spamKeywords), new TooLongTextAnalyzer(commentMaxLength),
        new NegativeTextAnalyzer() };
    TextAnalyzer[] textAnalyzers3 = { new TooLongTextAnalyzer(commentMaxLength), new SpamAnalyzer(spamKeywords),
        new NegativeTextAnalyzer() };
    TextAnalyzer[] textAnalyzers4 = { new TooLongTextAnalyzer(commentMaxLength), new NegativeTextAnalyzer(),
        new SpamAnalyzer(spamKeywords) };
    TextAnalyzer[] textAnalyzers5 = { new NegativeTextAnalyzer(), new SpamAnalyzer(spamKeywords),
        new TooLongTextAnalyzer(commentMaxLength) };
    TextAnalyzer[] textAnalyzers6 = { new NegativeTextAnalyzer(), new TooLongTextAnalyzer(commentMaxLength),
        new SpamAnalyzer(spamKeywords) };
    // dataset
    String[] tests = new String[8];
    tests[0] = "This comment is so good."; // OK
    tests[1] = "This comment is so Loooooooooooooooooooooooooooong."; // TOO_LONG
    tests[2] = "Very negative comment !!!!=(!!!!;"; // NEGATIVE_TEXT
    tests[3] = "Very BAAAAAAAAAAAAAAAAAAAAAAAAD comment with :|;"; // NEGATIVE_TEXT or TOO_LONG
    tests[4] = "This comment is so bad...."; // SPAM
    tests[5] = "The comment is a spam, maybeeeeeeeeeeeeeeeeeeeeee!"; // SPAM oTOO_LONG
    tests[6] = "Negative bad :( spam."; // SPAM oNEGATIVE_TEXT
    tests[7] = "Very bad, very neg =(, very .................."; // SPAM oNEGATIVE_TEXT or TOO_LONG
    TextAnalyzer[][] textAnalyzers = { textAnalyzers1, textAnalyzers2, textAnalyzers3, textAnalyzers4, textAnalyzers5,
        textAnalyzers6 };
    Main testObject = new Main();
    int numberOfAnalyzer; // the analyzer number specified in the identifier textAnalyzers {#}
    int numberOfTest = 0; // the number of the test, which corresponds to the index of test comments
    for (String test : tests) {
      numberOfAnalyzer = 1;
      System.out.print("test #" + numberOfTest + ": ");
      System.out.println(test);
      for (TextAnalyzer[] analyzers : textAnalyzers) {
        System.out.print(numberOfAnalyzer + ": ");
        System.out.println(testObject.checkLabels(analyzers, test));
        numberOfAnalyzer++;
      }
      numberOfTest++;
    }
  }

  public Label checkLabels(TextAnalyzer[] analyzers, String text) {
    for (TextAnalyzer analyzer : analyzers) {
      Label result = analyzer.processText(text);
      if (result != Label.OK)
        return result;
    }
    return Label.OK;
  }
}

class SpamAnalyzer extends KeywordAnalyzer {
  private final String[] keywords;

  public SpamAnalyzer(String[] words) {
    keywords = words;
  }

  protected String[] getKeywords() {
    return keywords;
  }

  protected Label getLabel() {
    return Label.SPAM;
  }
}

class NegativeTextAnalyzer extends KeywordAnalyzer {
  private final String[] keywords = { ":(", "=(", ":|" };

  protected String[] getKeywords() {
    return keywords;
  }

  protected Label getLabel() {
    return Label.NEGATIVE_TEXT;
  }
}

class TooLongTextAnalyzer implements TextAnalyzer {
  private final int maxLength;

  public TooLongTextAnalyzer(int maxLeng) {
    maxLength = maxLeng;
  }

  public Label processText(String text) {
    if (text.length() > maxLength)
      return Label.TOO_LONG;
    return Label.OK;
  }
}

abstract class KeywordAnalyzer implements TextAnalyzer {
  protected abstract String[] getKeywords();

  protected abstract Label getLabel();

  public Label processText(String text) {
    for (String keyword : this.getKeywords()) {
      if (text.contains(keyword))
        return this.getLabel();
    }
    return Label.OK;
  }
}