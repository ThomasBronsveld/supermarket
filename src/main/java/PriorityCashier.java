import java.time.LocalTime;

/**
 * @Project $(PROJECT_NAME)
 * @Author Thomas Bronsveld <Thomas.Bronsveld@hva.nl>
 */
public class PriorityCashier extends FIFOCashier {
    private final int maxNumPriorityItems;
    private Customer servingCustomer;

    public PriorityCashier(String name, int maxNumPriorityItems) {
        super(name);
        this.maxNumPriorityItems = maxNumPriorityItems;
    }
    @Override
    public int expectedWaitingTime(Customer customer) {
        int amount = 0;

        if(this.waitingQueue.size() != 0){
            for (Customer c : waitingQueue
            ) {
                amount += expectedCheckOutTime(c.getNumberOfItems());
            }
            if(this.servingCustomer != null){
                amount += this.expectedCheckOutTime(this.servingCustomer.getNumberOfItems()) - this.getCurrentTime().getSecond();
            }

        }
        if(this.waitingQueue.size() == 0 && this.servingCustomer != null){
            amount = this.expectedCheckOutTime(this.servingCustomer.getNumberOfItems()) - this.getCurrentTime().getSecond();
        }

        if(this.servingCustomer == null && customer.getNumberOfItems() < this.maxNumPriorityItems && this.waitingQueue.size() != 0){
            amount = 0;
        }

        return amount;
    }

    @Override
    public void doTheWorkUntil(LocalTime targetTime) {
        this.setCurrentTime(targetTime);

        if(this.servingCustomer == null && this.waitingQueue.size() == 0){
            this.setTotalIdleTime(this.getTotalIdleTime() + targetTime.getSecond());
        }

        if(this.waitingQueue.size() != 0 && this.servingCustomer == null){
            this.servingCustomer = this.waitingQueue.peek();
            this.waitingQueue.remove(this.waitingQueue.peek());
        }

    }
}
