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
}
