import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.util.List;

class weather extends TimerTask {

    private static int averageValueOfTemperature(int[] TemperatureArray) {
        int SumOfTemperature = 0;
        int Number_of_temperature_values = 0;

        for (int i = 0; i < TemperatureArray.length; i++) {
            SumOfTemperature += TemperatureArray[i];
            Number_of_temperature_values++;
        }
        return SumOfTemperature / Number_of_temperature_values;
    } //находим среднее значение температур;

    static boolean isSent = false;

    private static void isSendedTrue() {
        isSent = true;
    } //Этот метод говорит о том, что отправилось сообщение или нет;

    private static int[] temperatureValues(String str) {

        List<Integer> list = new ArrayList();

        Pattern patternTemperatureOneNumber = Pattern.compile("\\d\\s");
        Pattern patternTemperatureTwoNumber = Pattern.compile("\\d\\d");
        for (int i = 0; i < str.length(); i++) {

            if (str.substring(i, i + 1).equals("+")) {
                Matcher matcherForOneNumber = patternTemperatureOneNumber.matcher(str.substring(i + 1, i + 3));
                Matcher matcherForTwoNumbers = patternTemperatureTwoNumber.matcher(str.substring(i + 1, i + 3));
                if (matcherForOneNumber.find()) {
                    list.add(Integer.parseInt(str.substring(i + 1, i + 2)));
                }
                if (matcherForTwoNumbers.find()) {
                    list.add(Integer.parseInt(str.substring(i + 1, i + 3)));
                }

            }
            if (str.substring(i, i + 1).equals("-")) {
                Matcher matcherForOneNumber = patternTemperatureOneNumber.matcher(str.substring(i + 1, i + 3));
                Matcher matcherForTwoNumbers = patternTemperatureTwoNumber.matcher(str.substring(i + 1, i + 3));
                if (matcherForOneNumber.find()) {
                    list.add(Integer.parseInt(str.substring(i + 1, i + 2)) * -1);
                }
                if (matcherForTwoNumbers.find()) {
                    list.add(Integer.parseInt(str.substring(i + 1, i + 3)) * -1);
                }

            }

        }

        int[] numArray = new int[list.size()];
        int counter = 0;
        for (int numlist : list) {
            numArray[counter] = numlist;
            counter++;
        }
        return numArray;
    }//из строки находим значения температур и передаем их в массив;


    private static boolean pressureGrad(String pressure) {

        Pattern patternForSplitLetters = Pattern.compile("[A-Z]*[A-Z]\\s+|\\s+");

        String[] ValuePressure = patternForSplitLetters.split(pressure);


        Pattern patternForSplitValues = Pattern.compile("\\s\\d\\.\\d\\d(\\s|$)|\\d(\\s|$)");
        String[] PressureStringArray = patternForSplitValues.split(pressure);


        List <Double> ValueArray = new ArrayList();

        int ValueArrayCounter = 0;

        for (int i = 1; i < ValuePressure.length; i++) {

            if (ValuePressure[i].equals("")) {

                continue;
            }

            ValueArray.add(Double.parseDouble(ValuePressure[i]));
            i++;
        }

        int LetterCounter = 0;

        for (int j = 0; j < PressureStringArray.length; j++) {

            if (PressureStringArray[j].equals(" NW") || PressureStringArray[j].equals(" N") || PressureStringArray[j].equals(" W") || PressureStringArray[j].equals("NW") || PressureStringArray[j].equals("W") || PressureStringArray[j].equals("N") || PressureStringArray[j].equals("NW ") || PressureStringArray[j].equals("N ") || PressureStringArray[j].equals("W ") || PressureStringArray[j].equals(" NW ") || PressureStringArray[j].equals(" W ") || PressureStringArray[j].equals(" N ")) {
                LetterCounter++;
            }
        }//Мне кажется этот цикл можно как-то заменить. Скорее всего через pattern искать;


        int CounterForCycleToChekValueArray = 0;

        System.out.print("Pressure after split: ");

        for (int n = 0; n < ValueArray.size(); n++) {

            System.out.print(ValueArray.get(n) + " ");

            if (ValueArray.get(n) >= 0.03) {

                CounterForCycleToChekValueArray++;

            }
        }
        System.out.println();

        boolean isPrime;

        if (CounterForCycleToChekValueArray == ValueArray.size() && LetterCounter == PressureStringArray.length) {
            isPrime = true;
        } else {
            isPrime = false;
        }

        return isPrime;

    }//из строки получаем значения давлений и сравниваем их с заданным условием;


    private static void WhetherMain()
            throws MessagingException, IOException {


        Document pageOfSite = Jsoup.parse(new URL("http://nwwind.net/wind.php?region=3&sdate=2012-07-27"), 6000);
        Elements string = pageOfSite.select("tr").eq(26);
        String stringFromSWebpage = string.select("td").text();

        Pattern patternForTemperature = Pattern.compile("((\\+|\\-)\\d(\\d?)\\s){6,}|((\\+|\\-)\\d(\\d?)\\s){5,}|((\\+|\\-)\\d(\\d?)\\s){4,}");
        Pattern patternForPressure = Pattern.compile("\\s([A-Z][A-Z]\\s\\d\\.\\d\\d(\\s|$)+|\\d(\\s+|\\z)){2,}");


        Matcher matcherForTemperature = patternForTemperature.matcher(stringFromSWebpage);
        Matcher matcherForPressure = patternForPressure.matcher(stringFromSWebpage);

        matcherForPressure.find();

        String pressureFind = matcherForPressure.group();

        System.out.println("Pressure: " + pressureFind);

        matcherForTemperature.find();

        String temperature = matcherForTemperature.group();

        System.out.println("Temperature: " + temperature);

        boolean check = pressureGrad(pressureFind);

        if (check == true) {
            System.out.println("All pressure values > 0.3:" + check);
        }

        int[] temperatureArrayAfterConvert = temperatureValues(temperature);

        int averageTemperatureValue = averageValueOfTemperature(temperatureArrayAfterConvert);

        System.out.println("average value of temperature:" + averageTemperatureValue);

        System.out.println("//////////////////////////////////////////////////////////////////");


        if (check == true & averageTemperatureValue >= 10) {
            final Properties properties = new Properties();
            properties.put("mail.transport.protocol", "smtps");
            properties.put("mail.smtps.auth", true);
            properties.put("mail.smtps.host", "smtp.gmail.com");
            properties.put("mail.smtps.user", "testweatherjava");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.socketFactory.fallback", "false");

            Session mailSession = Session.getDefaultInstance(properties);
            MimeMessage message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress("aleck.kaplin@gmail.com"));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress("testweatherjava@gmail.com"));

            message.setSubject("weather");
            message.setText("Information about Port Angeles - Seattle:"
                    + "\n" + "average value of temperature:" + averageTemperatureValue + "\n" + "pressure gradients:" + pressureFind);

            Transport tr = mailSession.getTransport();
            tr.connect("aleck.kaplin@gmail.com", "Waweqtt541");

            tr.sendMessage(message, message.getAllRecipients());
            System.out.println("The message sent successfully!");
            tr.close();
            isSendedTrue();

        }//If average value of temperature more than ten and pressure values more than 0.3 program will send a message;
         // (блок с отправкой сообщения не работает, я гуглил и вроде как это ошибка из-за того что гугл не дает мне доступ, это стороннее приложения - вредоносное)

    } // тут связываются все методы в единое целое и находится блок кода для отправки сообщения на почту

    @Override
    public void run() {

        try {
            WhetherMain();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        } catch (IllegalStateException e) {
            System.out.println("Site just blow up, please wait...");
            System.out.println("//////////////////////////////////////////////////////////////////");
        }
    }
}


