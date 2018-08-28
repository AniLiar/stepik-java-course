import java.util.logging.*;

class Main {

  public static final String AUSTIN_POWERS = "Austin Powers";
  public static final String WEAPONS = "weapons";
  public static final String BANNED_SUBSTANCE = "banned substance";

  public static interface Sendable {
    String getFrom();
    String getTo();
  }

  public static abstract class AbstractSendable implements Sendable {
    protected final String from;
    protected final String to;

    public AbstractSendable(String from, String to) {
      this.from = from;
      this.to = to;
    }

    @Override
    public String getFrom() {
      return from;
    }

    @Override
    public String getTo() {
      return to;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;

      AbstractSendable that = (AbstractSendable) o;

      if (!from.equals(that.from))
        return false;
      if (!to.equals(that.to))
        return false;

      return true;
    }
  }

  /*
  Письмо, у которого есть текст, который можно получить с помощью метода `getMessage`
  */
  public static class MailMessage extends AbstractSendable {
    private final String message;

    public MailMessage(String from, String to, String message) {
      super(from, to);
      this.message = message;
    }

    public String getMessage() {
      return message;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      if (!super.equals(o))
        return false;

      MailMessage that = (MailMessage) o;

      if (message != null ? !message.equals(that.message) : that.message != null)
        return false;

      return true;
    }
  }

  /*
  * Посылка, содержимое которой можно получить с помощью метода `getContent`
  */
  public static class MailPackage extends AbstractSendable {
    private final Package content;

    public MailPackage(String from, String to, Package content) {
      super(from, to);
      this.content = content;
    }

    public Package getContent() {
      return content;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      if (!super.equals(o))
        return false;

      MailPackage that = (MailPackage) o;

      if (!content.equals(that.content))
        return false;

      return true;
    }
  }

  /*
  Класс, который задает посылку. У посылки есть текстовое описание содержимого и целочисленная ценность.
  */
  public static class Package {
    private final String content;
    private final int price;

    public Package(String content, int price) {
      this.content = content;
      this.price = price;
    }

    public String getContent() {
      return content;
    }

    public int getPrice() {
      return price;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;

      Package aPackage = (Package) o;

      if (price != aPackage.price)
        return false;
      if (!content.equals(aPackage.content))
        return false;

      return true;
    }
  }

  /*
  Интерфейс, который задает класс, который может каким-либо образом обработать почтовый объект.
  */
  public static interface MailService {
    Sendable processMail(Sendable mail);
  }

  /*
  Класс, в котором скрыта логика настоящей почты
  */
  public static class RealMailService implements MailService {

    @Override
    public Sendable processMail(Sendable mail) {
      // Здесь описан код настоящей системы отправки почты.
      return mail;
    }
  }

  public static class UntrustworthyMailWorker implements MailService {
    private final MailService[] mailServices;
    private static final RealMailService realMailService = new RealMailService();

    public UntrustworthyMailWorker(MailService[] mailServices) {
      this.mailServices = mailServices;
    }

    public RealMailService getRealMailService() {
      return realMailService;
    }

    @Override
    public Sendable processMail(Sendable mail) {
      Sendable returnedMail = mail;
      for (int i = 0; i < mailServices.length; i++) {
        returnedMail = mailServices[i].processMail(returnedMail);
      }
      getRealMailService().processMail(returnedMail);
      return mail;
    }
  }

  public static class Spy implements MailService {
    private final Logger log;
    
    public Spy(Logger log) {
      this.log = log;
    }

    private void logWarn(String from, String to, String message) {
      log.log(Level.WARNING, "Detected target mail correspondence: from " + from + " to " + to + " \"" + message + "\"");
    }

    private void logInfo(String from, String to) {
      log.log(Level.INFO, "Usual correspondence: from " + from + " to " + to);
    } 

    @Override
    public Sendable processMail(Sendable mail) {
      if (mail instanceof MailMessage) {
        if (mail.getFrom().equals(AUSTIN_POWERS) || mail.getTo().equals(AUSTIN_POWERS)) {
          logWarn(mail.getFrom(), mail.getTo(), ((MailMessage)mail).getMessage());
        } else {
          logInfo(mail.getFrom(), mail.getTo());
        }
      }
      return mail;
    }
  }

  public static class Thief implements MailService {
    private final int valueMin;
    private int totalStolen;

    public Thief(int valueMin) {
      this.valueMin = valueMin;
      this.totalStolen = 0;
    }

    public int getStolenValue() {
      return totalStolen;
    }

    private Sendable makeFakePackage(Sendable mail) {
        String fakeContent = "stones instead of " + ((MailPackage)mail).getContent().getContent(); 
        AbstractSendable fakePackage = new MailPackage(mail.getFrom(), mail.getTo(), 
                                                      new Package(fakeContent, 0));
        return fakePackage;
    }

    private void stealValue(int value) {
      totalStolen += value;
    }

    @Override
    public Sendable processMail(Sendable mail) {
      if (mail instanceof MailPackage) {
        if (((MailPackage)mail).getContent().getPrice() >= valueMin) {
            stealValue(((MailPackage)mail).getContent().getPrice());
            return makeFakePackage(mail);
        }   
      } 
      return mail;
    }
  }

  public static class Inspector implements MailService {
    private static final String[] ILLEGAL_CONTENT =
            new String[] {WEAPONS, BANNED_SUBSTANCE};

    @Override
    public Sendable processMail(Sendable mail) {
      if (mail instanceof MailPackage) {
        String content = ((MailPackage)mail).getContent().getContent();
        for (String illegalContent : ILLEGAL_CONTENT) {
          if (content.contains(illegalContent)) {
            throw new IllegalPackageException();
            //throw new IllegalPackageException("detected the illegal package!!!");
          }
        }
        if (content.contains("stones")) {
          throw new StolenPackageException();
          //throw new StolenPackageException("detected the stolen package!!!");
        }
      }
      return mail;
    }
  }

  public static class IllegalPackageException extends RuntimeException {
    // public IllegalPackageException(String message) {
      //    super(message);
      //}
  }

  public static class StolenPackageException extends RuntimeException {
      //public StolenPackageException(String message) {
      //    super(message);
      //}
  }

  public static void main(String[] args) {
    Logger logger = Logger.getLogger("main");

    Inspector inspector = new Inspector();
    Spy spy = new Spy(logger);

    int valuePackageMin = 100;
    Thief thief = new Thief(valuePackageMin);

    MailService variousWorkers[] = {spy, thief, inspector};
    UntrustworthyMailWorker worker = new UntrustworthyMailWorker(variousWorkers);

    AbstractSendable correspondence[] = { 
        new MailMessage("dog", "cat", "Hi!"),
        new MailMessage("cat", "dog", "Hi! What do you want?"),
        new MailMessage("cat", AUSTIN_POWERS, "Dog is writing me again. Can you do something?"),
        new MailMessage(AUSTIN_POWERS, "cat", "I'll do my best."),
        new MailPackage(AUSTIN_POWERS, "dog", new Package("bomb", 300)),
        new MailMessage("dog", AUSTIN_POWERS, "Why do you send me stones?"),
        new MailPackage(AUSTIN_POWERS, "dog", new Package("banned substance", 99)),
        new MailPackage(AUSTIN_POWERS, "dog", new Package("tiny bomb", 90)),
        new MailMessage(AUSTIN_POWERS, "cat", "Done."), 
    };

    for (AbstractSendable sendable:correspondence) {
      try {
        worker.processMail(sendable);
      } catch (StolenPackageException e) {
        logger.log(Level.WARNING, "Inspector found stolen package: " + e);
      } catch (IllegalPackageException e) {
        logger.log(Level.WARNING, "Inspector found illegal package: " + e);
      }
    }
  }
}