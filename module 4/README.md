#### 9 task from 4.3 module
��� ������� ��������� ���������� �� ��������� ���������� ���� ������� � ���������� ����������� � �������� ������-��������������� ��� � ��� ���� ��������� ������ ��� � ���������� � �����������.

��� ����� �������, ����������� ������ �������������� �������� �������.

��� ������ ���������� ���, ����������� ��� ������������ ��������.
```
/*
���������: ��������, ������� ����� ��������� �� �����.
� ����� �������� ����� �������� �� ���� � ���� ������������ ������.
*/
public static interface Sendable {
    String getFrom();
    String getTo();
}
```
� Sendable ���� ��� ����������, ������������ ��������� ����������� �������:
```
/*
����������� �����,������� ��������� �������������� ������ ��������
��������� � ���������� ������ � ��������������� ����� ������.
*/
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractSendable that = (AbstractSendable) o;

        if (!from.equals(that.from)) return false;
        if (!to.equals(that.to)) return false;

        return true;
    }
}
```
������ ����� ��������� ������� ������, � ������� ��������� ������ ��������� ���������.
```
/*
������, � �������� ���� �����, ������� ����� �������� � ������� ������ `getMessage`
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MailMessage that = (MailMessage) o;

        if (message != null ? !message.equals(that.message) : that.message != null) return false;

        return true;
    }
}
```
������ ����� ��������� �������� �������:
```
/*
�������, ���������� ������� ����� �������� � ������� ������ `getContent`
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MailPackage that = (MailPackage) o;

        if (!content.equals(that.content)) return false;

        return true;
    }
}
```
��� ���� ���� ������� ����������� ��������� �������:
```
/*
�����, ������� ������ �������. � ������� ���� ��������� �������� ����������� � ������������� ��������.
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Package aPackage = (Package) o;

        if (price != aPackage.price) return false;
        if (!content.equals(aPackage.content)) return false;

        return true;
    }
}
```
������ ���������� ������, ������� ���������� ������ ��������� �������:
```
/*
���������, ������� ������ �����, ������� ����� �����-���� ������� ���������� �������� ������.
*/
public static interface MailService {
    Sendable processMail(Sendable mail);
}

/*
�����, � ������� ������ ������ ��������� �����
*/
public static class RealMailService implements MailService {

    @Override
    public Sendable processMail(Sendable mail) {
        // ����� ������ ��� ��������� ������� �������� �����.
        return mail;
    }
}
```
��� ���������� ������� ����� �������, ������ �� ������� �������� MailService:

1) UntrustworthyMailWorker � �����, ������������ ����������� ��������� �����, ������� ������ ����, ����� �������� �������� ������ ��������������� � ������ �����, ��������������� �������� ���� ������ ������ ������� ���, � �����, � ����� ������, �������� ������������ ������ ��������������� ���������� RealMailService. � UntrustworthyMailWorker ������ ���� ����������� �� ������� MailService ( ��������� ������ processMail ������� �������� ������� ���������� �� ���� processMail ������� ��������, � �. �.) � ����� getRealMailService, ������� ���������� ������ �� ���������� ��������� RealMailService.

2) Spy � �����, ������� ��������� � ���� �������� ���������, ������� �������� ����� ��� ����. ������ �������������� �� ���������� Logger, � ������� �������� ����� ����� �������� � ���� ���������. �� ������ ������ �� ��������� ������ MailMessage � ����� � ������ ��������� ��������� (� ���������� ����� �������� ����� � �������� ������� �� �������� ����� �����):
2.1) ���� � �������� ����������� ��� ���������� ������ "Austin Powers", �� ����� �������� � ��� ��������� � ������� WARN: Detected target mail correspondence: from {from} to {to} "{message}"
2.2) �����, ���������� �������� � ��� ��������� � ������� INFO: Usual correspondence: from {from} to {to}

3) Thief � ���, ������� ������ ����� ������ ������� � ���������� ��� ���������. ��� ��������� � ������������ ���������� int � ����������� ��������� �������, ������� �� ����� ��������. �����, � ������ ������ ������ �������������� ����� getStolenValue, ������� ���������� ��������� ��������� ���� �������, ������� �� ��������. ��������� ���������� ��������� �������: ������ �������, ������� ������ ����, �� ������ �����, ����� ��, ������ � ������� ��������� � ���������� ������� "stones instead of {content}".

4) Inspector � ���������, ������� ������ �� ������������ � ����������� ��������� � ���� ������� � ���� ����������, ���� ���� ���������� �������� �������. ���� �� ������� ����������� ������� � ����� �� ����������� ���������� ("weapons" � "banned substance"), �� �� ������� IllegalPackageException. ���� �� ������� �������, ��������� �� ������ (�������� ����� "stones"), �� ������� ��������� � ���� StolenPackageException. ��� ���������� �� ������ �������� �������������� � ���� ������������� ����������.

��� ������ ������ ���� ���������� ��� ��������� � �����������, ��� ��� � �������� �������� ��� ��� ����� ���������� �� ������� �����, ������� ���������� ������������� � ��������� ���������. ��� �������� �� ������� ������ ��������� ��������� ������� �������� � ������������� ��� ���������� ������ java.util.logging. ��� �����������, �������� ��� ������� �������� Sendable ������ �������������� ���������� instanceof.
```
public static final String AUSTIN_POWERS = "Austin Powers";
public static final String WEAPONS = "weapons";
public static final String BANNED_SUBSTANCE = "banned substance";
```