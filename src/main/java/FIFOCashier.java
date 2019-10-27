import java.time.LocalTime;

/**
 * @Project $(PROJECT_NAME)
 * @Author Thomas Bronsveld <Thomas.Bronsveld@hva.nl>
 */
public class FIFOCashier extends Cashier {
    protected FIFOCashier(String name) {
        super(name);
    }

    @Override
    public int expectedCheckOutTime(int numberOfItems) {
        return 0;
    }

    @Override
    public int expectedWaitingTime(Customer customer) {
        return 0;
    }

    @Override
    public void doTheWorkUntil(LocalTime targetTime) {

    }

    public int checkoutTimePerCustomer(){
        return 0;
    }

    public int checkoutTimePerItem(){
        return 0;
    }
}
