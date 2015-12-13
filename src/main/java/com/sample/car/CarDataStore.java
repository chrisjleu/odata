package com.sample.car;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A crude implementation of an in-memory data store. 
 * 
 * Ordinarily this would be a component that handles CRUD operations on a (relational) database.
 */
public enum CarDataStore {

	INSTANCE;

	ConcurrentHashMap<Long, Map<String, Object>> memoryDataStore;

	private CarDataStore() {
		// Create an initial data set
		memoryDataStore = new ConcurrentHashMap<Long, Map<String, Object>>(5);
		Calendar date = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		memoryDataStore.put(1l, createCar(1, "X5", 1, 90000.00, "EUR", "2015", date, "file://imagePath/x5"));
		memoryDataStore.put(2l, createCar(2, "307", 2, 27000.00, "EUR", "2015", date, "file://imagePath/307"));
		memoryDataStore.put(3l, createCar(3, "GT", 2, 65995.00, "EUR", "2015", date, "file://imagePath/gt"));
		memoryDataStore.put(4l, createCar(4, "Stingray", 1, 79999.99, "EUR", "2015", date, "file://stingray"));
		memoryDataStore.put(5l, createCar(5, "Golf", 1, 18950.99, "EUR", "2015", date, "file://imagePath/golf"));
	}

	public Map<String, Object> getCar(final int id) {
		return memoryDataStore.get(id);
	}

	public String addCar(Map<String, Object> data) {
		// Horrible casting
		String model = (String) data.get("Model");
		int manId = (Integer) data.get("ManufacturerId");
		BigDecimal price = (BigDecimal) data.get("Price");
		String currency = (String) data.get("Currency");

		return addCar(model, manId, price.doubleValue(), currency, null, null);
	}

	public String addCar(final String model, final int manufacturerId, final double price, final String currency,
			final String modelYear, final String imagePath) {

		Calendar date = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		long id = date.getTimeInMillis();
		Map<String, Object> newCar = createCar(id, model, manufacturerId, price, currency, modelYear, date, imagePath);

		// Add new car to the data store
		memoryDataStore.put(id, newCar);

		// Return the id that was generated
		return String.valueOf(id);
	}

	private Map<String, Object> createCar(final long carId, final String model, final int manufacturerId,
			final double price, final String currency, final String modelYear, final Calendar updated,
			final String imagePath) {
		Map<String, Object> data = new HashMap<String, Object>();

		data.put("Id", carId);
		data.put("Model", model);
		data.put("ManufacturerId", manufacturerId);
		data.put("Price", price);
		data.put("Currency", currency);
		data.put("ModelYear", modelYear);
		data.put("Updated", updated);
		data.put("ImagePath", imagePath);

		return data;
	}

	public void deleteCar(final long carId) {
		memoryDataStore.remove(carId);
	}
	
	public Map<String, Object> getManufacturer(final int id) {
		Map<String, Object> data = null;
		Calendar date = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

		switch (id) {
		case 1:
			Map<String, Object> addressStar = createAddress("Star Street 137", "Stuttgart", "70173", "Germany");
			date.set(1954, 7, 4);
			data = createManufacturer(1, "Star Powered Racing", addressStar, date);
			break;

		case 2:
			Map<String, Object> addressHorse = createAddress("Horse Street 1", "Maranello", "41053", "Italy");
			date.set(1929, 11, 16);
			data = createManufacturer(2, "Horse Powered Racing", addressHorse, date);
			break;

		default:
			break;
		}

		return data;
	}

	private Map<String, Object> createManufacturer(final int id, final String name, final Map<String, Object> address,
			final Calendar updated) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("Id", id);
		data.put("Name", name);
		data.put("Address", address);
		data.put("Updated", updated);
		return data;
	}

	private Map<String, Object> createAddress(final String street, final String city, final String zipCode,
			final String country) {
		Map<String, Object> address = new HashMap<String, Object>();
		address.put("Street", street);
		address.put("City", city);
		address.put("ZipCode", zipCode);
		address.put("Country", country);
		return address;
	}

	public List<Map<String, Object>> getCars() {
		return new ArrayList<Map<String, Object>>(memoryDataStore.values());
	}

	public List<Map<String, Object>> getManufacturers() {
		List<Map<String, Object>> manufacturers = new ArrayList<Map<String, Object>>();
		manufacturers.add(getManufacturer(1));
		manufacturers.add(getManufacturer(2));
		return manufacturers;
	}

	public List<Map<String, Object>> getCarsFor(final int manufacturerId) {
		List<Map<String, Object>> cars = getCars();
		List<Map<String, Object>> carsForManufacturer = new ArrayList<Map<String, Object>>();

		for (Map<String, Object> car : cars) {
			if (Integer.valueOf(manufacturerId).equals(car.get("ManufacturerId"))) {
				carsForManufacturer.add(car);
			}
		}

		return carsForManufacturer;
	}

	public Map<String, Object> getManufacturerFor(final int carId) {
		Map<String, Object> car = getCar(carId);
		if (car != null) {
			Object manufacturerId = car.get("ManufacturerId");
			if (manufacturerId != null) {
				return getManufacturer((Integer) manufacturerId);
			}
		}
		return null;
	}
}
