package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {
	private final int number = Math.abs(new Random().nextInt());
	private Map<Product, Integer> products = new LinkedHashMap<Product, Integer>();

	public void addProduct(Product product) {
		addProduct(product, 1);
	}

	public void addProduct(Product product, Integer quantity) {
		if (product == null || quantity <= 0) {
			throw new IllegalArgumentException();
		}
		Product existingProduct = null;
		for (Product key : products.keySet()) {
			if (key.getName().equals(product.getName())) {
				existingProduct = key;
			}
		}
		if (existingProduct != null) {
			products.put(existingProduct, products.get(existingProduct) + quantity);
		} else {
			products.put(product, quantity);
		}
	}

	public BigDecimal getNetTotal() {
		BigDecimal totalNet = BigDecimal.ZERO;
		for (Product product : products.keySet()) {
			BigDecimal quantity = new BigDecimal(products.get(product));
			totalNet = totalNet.add(product.getPrice().multiply(quantity));
		}
		return totalNet;
	}

	public BigDecimal getTaxTotal() {
		return getGrossTotal().subtract(getNetTotal());
	}

	public BigDecimal getGrossTotal() {
		BigDecimal totalGross = BigDecimal.ZERO;
		for (Product product : products.keySet()) {
			BigDecimal quantity = new BigDecimal(products.get(product));
			totalGross = totalGross.add(product.getPriceWithTax().multiply(quantity));
		}
		return totalGross;
	}

	public int getNumber() {
		return number;
	}

	public String getInvoiceAsText() {
		List<String> lines = new ArrayList<>();
		lines.add("" + number);
		for (Map.Entry<Product, Integer> entry : products.entrySet()) {
			lines.add(entry.getKey().getName() + "," + entry.getKey().getPrice().setScale(2) + "," + entry.getValue());
		}
		lines.add("Liczba pozycji: " + products.size());
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lines.size(); i++) {
			sb.append(lines.get(i));
			if (i != lines.size() - 1) {
				sb.append('\n');
			}
		}
		return sb.toString();
	}
}
