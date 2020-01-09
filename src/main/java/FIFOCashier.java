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
        int amount = 0;
        //There is a serving customer & the queue is not 0.
        if (this.waitingQueue.size() != 0) {
            System.out.println("Eerste");
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
            System.out.println("dwdwadwawdwd");
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

        // Eerst komt de null klant
        // Dan komt de eerst volgende klant.
        //

        //werking

        //Do the work until
        //Ga de hele checklist af.
        //Klant toegevoegd aan waiting queue
        //Als klant nog bezig is, voeg klant van deze  nummer toe aan de queue
        //Do the work untill
        //Klant toegevoegd aan waiting queue

        while (this.currentTime.isBefore(targetTime)) {

            //check altijd de waitingQueue length.
            System.out.println("Lengte queue: " + this.waitingQueue.size());
            if(this.servingCustomer != null) {
                if(maxQueueLength < this.waitingQueue.size() + 1) {
                    maxQueueLength = this.waitingQueue.size() + 1;
                }
            } else if(maxQueueLength < this.waitingQueue.size()) {
                maxQueueLength = this.waitingQueue.size();
            }
//            if(this.servingCustomer != null || !this.waitingQueue.isEmpty()) {
//                System.out.println("hiertje");
//                if(this.maxQueueLength < this.waitingQueue.size()) {
//                    this.maxQueueLength = this.waitingQueue.size();
//                }
//            }

            //continue checkout of serving customer
            //if any servingCustomer = DONE
            //continue = the second check.
            //Check if the actual checkout time is shorter than expected checkout time. This is done so that we know we need to continue with the serving customer.
            //Otherwise we need to start serving this customer.
            if (this.servingCustomer != null && this.servingCustomer.getActualCheckOutTime() < this.expectedCheckOutTime(this.servingCustomer.getNumberOfItems())) {
                System.out.println("lol");

                //If we finish the checkout of current serving customer.
                if(this.currentTime.plusSeconds(this.servingCustomer.getActualCheckOutTime()).isBefore(targetTime)) {
                    currentTime = this.currentTime.plusSeconds(this.servingCustomer.getActualCheckOutTime());
                    this.servingCustomer = null;
                    continue;
                }

                //continue the checkout of current serving customer
                //Set actual checkout Time minus the difference between target Time and currentTime.
                this.servingCustomer.setActualCheckOutTime(this.servingCustomer.getActualCheckOutTime() - (int) ChronoUnit.SECONDS.between(targetTime, this.currentTime));
                this.currentTime = targetTime;
                break;

                //start serving new customer
            } else if(this.servingCustomer != null) {
                System.out.println("eyooyooy");
                //Set the expected checkout time.
                this.servingCustomer.setActualCheckOutTime(this.expectedCheckOutTime(this.servingCustomer.getNumberOfItems()));

                //If we can finish checking the customer out in a single rotation.
                if(this.currentTime.plusSeconds(this.servingCustomer.getActualCheckOutTime()).isBefore(targetTime)) {
                    currentTime = this.currentTime.plusSeconds(this.servingCustomer.getActualCheckOutTime());
                    this.servingCustomer = null;
                    continue;
                }

                //We can't finish checking the customer out before targetTime. Reduce the checkout time to know
                //how many seconds we have left checking the customers items out.
                this.servingCustomer.setActualCheckOutTime((int) ChronoUnit.SECONDS.between(targetTime, this.currentTime));
                this.currentTime = targetTime;
            }


            //check if we need to get new customer.
            if(this.servingCustomer == null) {
                System.out.println("lul");
                //If the queue is empty as well we add idle time, otherwise get new customer.
                if(this.waitingQueue.isEmpty()){
                    System.out.println("testerino");
                    this.setTotalIdleTime(this.getTotalIdleTime() + (int) ChronoUnit.SECONDS.between(this.getCurrentTime(), targetTime));
                    this.currentTime = targetTime;
                    continue;
                }
                //Pick new customer
                System.out.println("Hierzo??");
                this.servingCustomer = this.waitingQueue.poll();
                //Set the checkoutTime so that we can keep track of the progress.
                this.servingCustomer.setActualCheckOutTime(this.expectedCheckOutTime(this.servingCustomer.getNumberOfItems()));
            }
        }


//        //all cashiers proceed their work until targetTime
//        while (this.currentTime.isBefore(targetTime)) {
//            // continue or finish checkout of the current customer if there is any customer, but no longer than until the targetTime
//            if (this.servingCustomer != null) {
//                //continue check-out of customer.
//                if (this.expectedCheckOutTime(this.servingCustomer.getNumberOfItems()) < (int) ChronoUnit.SECONDS.between(this.getCurrentTime(), targetTime)) {
//                    this.currentTime.plusSeconds(this.expectedCheckOutTime(servingCustomer.getNumberOfItems()));
//                }
//                (int) ChronoUnit.SECONDS.between(this.getCurrentTime(), targetTime)
//                //Finish check-out of current serving customer.
//                this.currentTime = targetTime;
//            }
//
//            // if there is time left go next
//            //if the queue is empty, take a break
//            if (this.waitingQueue.isEmpty()) {
//                this.setTotalIdleTime(this.getTotalIdleTime() + (int) ChronoUnit.SECONDS.between(this.getCurrentTime(), targetTime));
//            }
//        }
//        //else go to the next customer from the queue and go through this process again
//        //else it means time = T (???)
////        System.out.println(targetTime);
//
//        while (this.currentTime.isBefore(targetTime)) {
//            //There is nobody in the waiting quee.
//            if (this.waitingQueue.isEmpty()) {
//                //There is no customer being served
//                if (this.servingCustomer == null) {
//                    this.currentTime = targetTime;
//                }
//            }
//            //There is a customer being served, but the waiting queue is empty.
//            else if (this.servingCustomer != null) {
//
//                if (this.expectedCheckOutTime(this.servingCustomer.getNumberOfItems()) < (int) ChronoUnit.SECONDS.between(this.getCurrentTime(), targetTime)) {
//                    this.waitingQueue.remove(servingCustomer);
//                    this.servingCustomer = null;
//                    continue;
//                }
//                this.currentTime = targetTime;
//                continue;
//            }
//
//            //The waiting queue is empty and there is no customer being served.
//            else {
//                maxQueueLength++;
//                this.servingCustomer = waitingQueue.poll();
//                this.currentTime = this.currentTime.plusSeconds(this.expectedCheckOutTime(servingCustomer.getNumberOfItems()));
//                this.servingCustomer = null;
//            }
//
//        }
    }
}
