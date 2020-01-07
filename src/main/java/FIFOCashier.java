import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * @Project $(PROJECT_NAME)
 * @Author Thomas Bronsveld <Thomas.Bronsveld@hva.nl>
 */
public class FIFOCashier extends Cashier {
    private Customer servingCustomer;

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
        //There is a serving customer & the queue is not 0.
        if (this.waitingQueue.size() != 0) {
            for (Customer c : waitingQueue
            ) {
                amount += expectedCheckOutTime(c.getNumberOfItems());
            }
            if (this.servingCustomer != null) {
                amount += this.expectedCheckOutTime(this.servingCustomer.getNumberOfItems()) - this.getCurrentTime().getSecond();
            }
        }
        //This condition goes if both the waiting queue is 0 and the cashier is serving a customer.
        if (this.waitingQueue.size() == 0 && this.servingCustomer != null) {
            amount = this.expectedCheckOutTime(this.servingCustomer.getNumberOfItems()) - this.getCurrentTime().getSecond();
        }

        //Werkt
        if (this.waitingQueue.size() == 0 && this.servingCustomer == null) {
            amount = 0;
        }
        return amount;
    }

    @Override
    public void doTheWorkUntil(LocalTime targetTime) {
        //all cashiers proceed their work until targetTime
        // continue or finish checkout of the current customer if there is any customer, but no longer than until the targetTime
        // if there is time left go next
        //if the queue is empty, take a break
        //else go to the next customer from the queue and go through this process again
        //else it means time = T (???)

        while (this.currentTime.isBefore(targetTime)) {
            if (this.waitingQueue.isEmpty()) {
                if (this.servingCustomer == null) {
                    this.setTotalIdleTime(this.getTotalIdleTime() + (int) ChronoUnit.SECONDS.between(this.getCurrentTime(), targetTime));
                }
            } else {
                this.servingCustomer = waitingQueue.peek();
                this.waitingQueue.remove(this.waitingQueue.peek());
                this.setCurrentTime(this.getCurrentTime().plusSeconds(this.expectedCheckOutTime(this.servingCustomer.getNumberOfItems())));
                continue;
            }
            this.setCurrentTime(targetTime);
        }


//        if(this.servingCustomer == null && this.waitingQueue.size() == 0){
//
//        }
//
//        if(this.waitingQueue.size() != 0 && this.servingCustomer == null){
//            this.servingCustomer = this.waitingQueue.peek();
//            this.setCurrentTime(this.getCurrentTime().plusSeconds(this.expectedCheckOutTime(this.servingCustomer.getNumberOfItems())));
//            this.waitingQueue.remove(this.waitingQueue.peek());
//            this.servingCustomer = null;
//
//            if(this.waitingQueue.size() == 0){
//                this.setTotalIdleTime(this.getTotalIdleTime() + (int) ChronoUnit.SECONDS.between(this.getCurrentTime(), targetTime));
//            }
//        }
//        this.setCurrentTime(targetTime);
    }
}
