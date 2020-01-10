import java.time.LocalTime;
import java.util.*;

/**
 * @Project $(PROJECT_NAME)
 * @Author Thomas Bronsveld <Thomas.Bronsveld@hva.nl>
 */
public class PriorityCashier extends FIFOCashier {
    private final int MAX_NUM_PRIORITY_ITEMS;
    private Customer servingCustomer;

    public PriorityCashier(String name, int MAX_NUM_PRIORITY_ITEMS) {
        super(name);
        this.MAX_NUM_PRIORITY_ITEMS = MAX_NUM_PRIORITY_ITEMS;
    }

    @Override
    public int expectedWaitingTime(Customer customer) {


        int amount = 0;
        Queue<Customer> holdingQeue = new LinkedList<>(this.waitingQueue);

        if(this.getServingCustomer() != null) {
            this.servingCustomer = this.getServingCustomer();
            amount = this.servingCustomer.getActualCheckOutTime();
        }

        System.out.println(amount);
        //There is a serving customer & the queue is not 0.
        if (holdingQeue.size() != 0) {
            if(customer.getNumberOfItems() <= MAX_NUM_PRIORITY_ITEMS) {
                holdingQeue.removeIf(cus -> cus.getNumberOfItems() > MAX_NUM_PRIORITY_ITEMS);
            }
            for (Customer c : holdingQeue
            ) {
                amount += expectedCheckOutTime(c.getNumberOfItems());
            }
        }
        //This condition goes if both the waiting queue is 0 and the cashier is serving a customer.
        if (holdingQeue.size() == 0 && this.servingCustomer != null) {
            amount = this.servingCustomer.getActualCheckOutTime();
        }

        if (holdingQeue.size() == 0 && this.servingCustomer == null) {
            amount = 0;
        }
        return amount;
    }
}
