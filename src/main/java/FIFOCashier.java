import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * @Project $(PROJECT_NAME)
 * @Author Thomas Bronsveld <Thomas.Bronsveld@hva.nl>
 */
public class FIFOCashier extends Cashier {
    public FIFOCashier(String name) {
        super(name);
    }

    @Override
    public int expectedCheckOutTime(int numberOfItems) {
        int secondsPerItem = 2;
        if (numberOfItems != 0) {
            return secondsPerItem * numberOfItems + 20;
        }
        return 0;
    }

    @Override
    public int expectedWaitingTime(Customer customer) {
        int amount = 0;
        if (this.waitingQueue.size() != 0) {
            for (Customer c : waitingQueue
            ) {
                amount += expectedCheckOutTime(c.getNumberOfItems());
            }
        }
        return amount;
    }

    @Override
    public void doTheWorkUntil(LocalTime targetTime) {
        long between = ChronoUnit.SECONDS.between(this.currentTime, targetTime);

//        long end = System.currentTimeMillis() + (between * 1000);

//        while (System.currentTimeMillis() < end) {
//            if (this.waitingQueue.size() == 0) {
//                this.setTotalIdleTime((int) (end - System.currentTimeMillis()) / 1000);
//                this.setCurrentTime(this.currentTime.plusSeconds(between));
//                break;
//            }
//
//            int time = this.expectedWaitingTime(this.waitingQueue.element());
//            this.setCurrentTime(this.currentTime.plusSeconds(time));
//            this.waitingQueue.remove(this.waitingQueue.element());
//        }

    }
}
