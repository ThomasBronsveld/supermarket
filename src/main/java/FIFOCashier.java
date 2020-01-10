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

    public Customer getServingCustomer() {
        return servingCustomer;
    }

    @Override
    public int expectedWaitingTime(Customer customer) {

        int amount;
        if(this.servingCustomer == null) {
            amount = 0;
        } else {
            amount = this.servingCustomer.getActualCheckOutTime();
        }

        //There is a serving customer & the queue is not 0.
        if (this.waitingQueue.size() != 0) {

            for (Customer c : waitingQueue
            ) {
                amount += expectedCheckOutTime(c.getNumberOfItems());
            }
        }
        //This condition goes if both the waiting queue is 0 and the cashier is serving a customer.
        if (this.waitingQueue.size() == 0 && this.servingCustomer != null) {
            amount = this.expectedCheckOutTime(this.servingCustomer.getNumberOfItems()) - this.getCurrentTime().getSecond();
        }

        if (this.waitingQueue.size() == 0 && this.servingCustomer == null) {
            amount = 0;
        }
        return amount;
    }

    @Override
    public void doTheWorkUntil(LocalTime targetTime) {

        while (this.currentTime.isBefore(targetTime)) {
            if(this.servingCustomer != null) {
                if(maxQueueLength < this.waitingQueue.size() + 1) {
                    maxQueueLength = this.waitingQueue.size() + 1;
                }
            } else if(maxQueueLength < this.waitingQueue.size()) {
                maxQueueLength = this.waitingQueue.size();
            }

            //continue checkout of serving customer
            //if any servingCustomer = DONE
            //continue = the second check.
            //Check if the actual checkout time is shorter than expected checkout time. This is done so that we know we need to continue with the serving customer.
            //Otherwise we need to start serving this customer.
            if (this.servingCustomer != null && this.servingCustomer.getActualCheckOutTime() < this.expectedCheckOutTime(this.servingCustomer.getNumberOfItems())) {

                //If we finish the checkout of current serving customer.
                if(this.currentTime.plusSeconds(this.servingCustomer.getActualCheckOutTime()).isBefore(targetTime)) {
                    currentTime = this.currentTime.plusSeconds(this.servingCustomer.getActualCheckOutTime());
                    this.servingCustomer = null;
                    continue;
                }

                //continue the checkout of current serving customer
                //Set actual checkout Time minus the difference between target Time and currentTime.

                this.servingCustomer.setActualCheckOutTime(this.servingCustomer.getActualCheckOutTime() - (int) ChronoUnit.SECONDS.between(this.currentTime, targetTime));
                this.currentTime = targetTime;
                break;

                //start serving new customer
            } else if(this.servingCustomer != null) {
                //If we can finish checking the customer out in a single rotation.
                if(this.currentTime.plusSeconds(this.servingCustomer.getActualCheckOutTime()).isBefore(targetTime)) {
                    currentTime = this.currentTime.plusSeconds(this.servingCustomer.getActualCheckOutTime());
                    this.servingCustomer = null;
                    continue;
                }

                //We can't finish checking the customer out before targetTime. Reduce the checkout time to know
                //how many seconds we have left checking the customers items out.
                this.servingCustomer.setActualCheckOutTime(this.servingCustomer.getActualCheckOutTime() - (int) ChronoUnit.SECONDS.between(this.currentTime, targetTime));
                this.currentTime = targetTime;
            }


            //check if we need to get new customer.
            if(this.servingCustomer == null) {
                //If the queue is empty as well we add idle time, otherwise get new customer.
                if(this.waitingQueue.isEmpty()){
                    this.setTotalIdleTime(this.getTotalIdleTime() + (int) ChronoUnit.SECONDS.between(this.getCurrentTime(), targetTime));
                    this.currentTime = targetTime;
                    continue;
                }

                //Pick new customer
                this.servingCustomer = this.waitingQueue.poll();
                //Set the checkoutTime so that we can keep track of the progress.
                this.servingCustomer.setActualCheckOutTime(this.expectedCheckOutTime(this.servingCustomer.getNumberOfItems()));
            }
        }
    }
}
