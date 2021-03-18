package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.mwo.invoice.product.BottleOfWine;
import pl.edu.agh.mwo.invoice.product.DairyProduct;
import pl.edu.agh.mwo.invoice.product.FuelCanister;
import pl.edu.agh.mwo.invoice.product.OtherProduct;
import pl.edu.agh.mwo.invoice.product.Product;
import pl.edu.agh.mwo.invoice.product.TaxFreeProduct;

public class InvoiceTest {
    private Invoice invoice;

    @Before
    public void createEmptyInvoiceForTheTest() {
        invoice = new Invoice();
    }

    @Test
    public void testEmptyInvoiceHasEmptySubtotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTaxAmount() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
        Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
        invoice.addProduct(taxFreeProduct);
        Assert.assertThat(invoice.getNetTotal(), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasProperSubtotalForManyProducts() {
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
        invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
        invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("310"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasProperTaxValueForManyProduct() {
        // tax: 0
        invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
        // tax: 8
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
        // tax: 2.30
        invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("10.30"), Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testInvoiceHasProperTotalValueForManyProduct() {
        // price with tax: 200
        invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200")));
        // price with tax: 108
        invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("100")));
        // price with tax: 12.30
        invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("320.30"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasPropoerSubtotalWithQuantityMoreThanOne() {
        // 2x kubek - price: 10
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        // 3x kozi serek - price: 30
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        // 1000x pinezka - price: 10
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("50"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasPropoerTotalWithQuantityMoreThanOne() {
        // 2x chleb - price with tax: 10
        invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
        // 3x chedar - price with tax: 32.40
        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
        // 1000x pinezka - price with tax: 12.30
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("54.70"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithZeroQuantity() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithNegativeQuantity() {
        invoice.addProduct(new DairyProduct("Zsiadle mleko", new BigDecimal("5.55")), -1);
    }

	@Test
	public void testInvoiceHasNumber() {
		int number = invoice.getNumber();
		Assert.assertTrue(number > 0);
	}

	@Test
	public void testTwoInvoicesHaveDifferentNumber() {
		int number = invoice.getNumber();
		int number2 = new Invoice().getNumber();
		Assert.assertNotEquals(number, number2);
	}
	
	@Test
	public void testTheSameInvoicesHasTheSameNumber() {
		Assert.assertEquals(invoice.getNumber(), invoice.getNumber());
	}

	@Test
	public void testInvoiceAsTextIsCorrect() {

		final String productOneName = "Owoce";
		final BigDecimal productOnePrice = new BigDecimal("200").setScale(2);

		final String productTwoName = "Maslanka";
		final BigDecimal productTwoPrice = new BigDecimal("200").setScale(2);
		final Integer productTwoQuantity = 3;

		final String productThreeName = "Wino";
		final BigDecimal productThreePrice = new BigDecimal("343.24").setScale(2);
		final Integer productThreeQuantity = 100;

		final String expectedSecondLine = productOneName + "," + productOnePrice + ",1";
		final String expectedThirdLine = productTwoName + "," + productTwoPrice + "," + productTwoQuantity;
		final String expectedFourthLine = productThreeName + "," + productThreePrice + "," + productThreeQuantity;

		invoice.addProduct(new TaxFreeProduct(productOneName, productOnePrice));
		invoice.addProduct(new DairyProduct(productTwoName, productTwoPrice), productTwoQuantity);
		invoice.addProduct(new OtherProduct(productThreeName, productThreePrice), productThreeQuantity);

		String invoiceAsText = invoice.getInvoiceAsText();
		String[] lines = invoiceAsText.split("\n");
		Assert.assertEquals(invoice.getNumber(), Integer.parseInt(lines[0]));
		Assert.assertEquals(expectedSecondLine, lines[1]);
		Assert.assertEquals(expectedThirdLine, lines[2]);
		Assert.assertEquals(expectedFourthLine, lines[3]);
		Assert.assertEquals("Liczba pozycji: " + 3, lines[lines.length - 1]);
	}

	@Test
	public void testProductIsNotDuplicated() {

		final String productOneName = "Owoce";
		final BigDecimal productOnePrice = new BigDecimal("200");

		final String productTwoName = "Owoce";
		final BigDecimal productTwoPrice = new BigDecimal("200");
		final Integer productTwoQuantity = 100;

		invoice.addProduct(new TaxFreeProduct(productOneName, productOnePrice));
		invoice.addProduct(new TaxFreeProduct(productTwoName, productTwoPrice), productTwoQuantity);

		String expectedInvoiceAsText = invoice.getNumber() + "\nOwoce,200.00,101\nLiczba pozycji: 1";
		Assert.assertEquals(expectedInvoiceAsText, invoice.getInvoiceAsText());
	}

	@Test
	public void testExciseIsAdded() {

		final String productOneName = "Wino";
		final BigDecimal productOnePrice = new BigDecimal("200");

		final String productTwoName = "Kanister paliwa";
		final BigDecimal productTwoPrice = new BigDecimal("100");
		final Integer productTwoQuantity = 100;

		invoice.addProduct(new BottleOfWine(productOneName, productOnePrice));
		invoice.addProduct(new FuelCanister(productTwoName, productTwoPrice), productTwoQuantity);

		final BigDecimal expectedProductOnePrice = new BigDecimal("205.56").setScale(2);
		final BigDecimal expectedProductTwoPrice = new BigDecimal("105.56").setScale(2);

		String expectedInvoiceAsText = invoice.getNumber()
				+ "\nWino," + expectedProductOnePrice + ",1\nKanister paliwa," + expectedProductTwoPrice
				+ ",100\nLiczba pozycji: 2";
		Assert.assertEquals(expectedInvoiceAsText, invoice.getInvoiceAsText());
	}
}
