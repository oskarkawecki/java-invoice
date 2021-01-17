package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public abstract class Product {
    private final String name;

    private final BigDecimal price;

    private final BigDecimal taxPercent;

    protected Product(String name, BigDecimal price, BigDecimal tax) {
		if (name == null || name.equals("")) {
			throw new IllegalArgumentException("You cannot create products with null or empty name");
		}

		if (name == null || price.signum() == -1) {
			throw new IllegalArgumentException("You cannot create products with null or empty name");
		}

		this.name = name;
        this.price = price;
        this.taxPercent = tax;
    }

    public String getName() {
		return this.name;
    }

    public BigDecimal getPrice() {
		return price;
    }

    public BigDecimal getTaxPercent() {
		return taxPercent;
    }

    public BigDecimal getPriceWithTax() {
		return this.price.multiply(this.taxPercent).add(this.price);
    }
}
