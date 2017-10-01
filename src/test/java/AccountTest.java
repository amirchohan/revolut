import org.junit.*;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class AccountTest {

    private Account acc_A, acc_B, acc_C;

    @Before
    public void setUp() throws Exception {
        acc_A = new Account(new BigDecimal("45.69"));
        acc_B = new Account(new BigDecimal("9234356.48"));
        acc_C = new Account(new BigDecimal("0.2"));
    }

    @Test
    public void testExistenceInDB() throws Exception {
        Assert.assertTrue(DB.checkIfAccountExists(acc_A.getId()));
        Assert.assertTrue(DB.checkIfAccountExists(acc_B.getId()));
        Assert.assertTrue(DB.checkIfAccountExists(acc_C.getId()));
    }

    @Test
    public void deposit_valid() throws Exception {
        Transaction transactionA = new Transaction(-1, acc_A.getId(), new BigDecimal("45.69"));
        acc_A.deposit(transactionA);
        Assert.assertEquals(new BigDecimal("91.38"), acc_A.getBalance());
        Assert.assertTrue(acc_A.getTransactions().contains(transactionA));

        Transaction transactionB = new Transaction(-1, acc_C.getId(), new BigDecimal("0.8"));
        acc_C.deposit(transactionB);
        Assert.assertEquals(new BigDecimal("1.0"), acc_C.getBalance());
        Assert.assertTrue(acc_C.getTransactions().contains(transactionB));

        Transaction transactionC = new Transaction(-1, acc_A.getId(), new BigDecimal("0.8"));
        acc_A.deposit(transactionC);
        Assert.assertEquals(new BigDecimal("92.18"), acc_A.getBalance());
        Assert.assertTrue(acc_A.getTransactions().contains(transactionA));
        Assert.assertTrue(acc_A.getTransactions().contains(transactionC));
    }

    @Test
    public void deposit_invalid() throws Exception {
        Exception exp = new Exception();
        Transaction transactionA = new Transaction(-1, -1, new BigDecimal("45.69"));
        try {
            acc_A.deposit(transactionA);
        }
        catch (Exception e) {
            exp = e;
        }
        Assert.assertEquals(
                "Invalid transaction: Destination Account ID doesn't match the account ID",
                exp.getMessage());

        exp = new Exception();
        transactionA = new Transaction(-1, acc_A.getId(), new BigDecimal("-45.69"));
        try {
            acc_A.deposit(transactionA);
        }
        catch (Exception e) {
            exp = e;
        }
        Assert.assertEquals(
                "Invalid transaction: Deposit amount must be positive",
                exp.getMessage());
    }



    @Test
    public void withdraw_valid() throws Exception {
        Transaction transactionA = new Transaction(acc_A.getId(),-1, new BigDecimal("45.69"));
        acc_A.withdraw(transactionA);
        Assert.assertEquals(new BigDecimal("0.00"), acc_A.getBalance());
        Assert.assertTrue(acc_A.getTransactions().contains(transactionA));

        Transaction transactionB = new Transaction(acc_C.getId(), -1, new BigDecimal("0.1"));
        acc_C.withdraw(transactionB);
        Assert.assertEquals(new BigDecimal("0.1"), acc_C.getBalance());
        Assert.assertTrue(acc_C.getTransactions().contains(transactionB));

        Transaction transactionC = new Transaction(acc_B.getId(), -1, new BigDecimal("100986"));
        acc_B.withdraw(transactionC);
        Assert.assertEquals(new BigDecimal("9133370.48"), acc_B.getBalance());
        Assert.assertTrue(acc_B.getTransactions().contains(transactionC));
    }



    @Test
    public void withdraw_invalid() throws Exception {
        Exception exp = new Exception();
        Transaction transactionA = new Transaction(-1, -1, new BigDecimal("45.69"));
        try {
            acc_A.withdraw(transactionA);
        }
        catch (Exception e) {
            exp = e;
        }
        Assert.assertEquals(
                "Invalid transaction: Source Account ID doesn't match the account ID",
                exp.getMessage());


        exp = new Exception();
        transactionA = new Transaction(acc_A.getId(),-1, new BigDecimal("-45.69"));
        try {
            acc_A.withdraw(transactionA);
        }
        catch (Exception e) {
            exp = e;
        }
        Assert.assertEquals(
                "Invalid transaction: Withdrawal amount must be positive",
                exp.getMessage());


        exp = new Exception();
        transactionA = new Transaction(acc_A.getId(),-1, new BigDecimal("0"));
        try {
            acc_A.withdraw(transactionA);
        }
        catch (Exception e) {
            exp = e;
        }
        Assert.assertEquals(
                "Invalid transaction: Withdrawal amount must be positive",
                exp.getMessage());



        exp = new Exception();
        transactionA = new Transaction(acc_A.getId(),-1, new BigDecimal("47"));
        try {
            acc_A.withdraw(transactionA);
        }
        catch (Exception e) {
            exp = e;
        }
        Assert.assertEquals(
                "Insufficient funds",
                exp.getMessage());
    }

}