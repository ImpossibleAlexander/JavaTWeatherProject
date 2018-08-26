import java.util.Timer;

class FixedRateScheduler{
    public static void main(String[] args) {
        Timer timer = new Timer();
        weather wth = new weather();
        timer.scheduleAtFixedRate(wth, 0, 3600000);
        if(weather.isSent)
            wth.cancel();
    }
}
