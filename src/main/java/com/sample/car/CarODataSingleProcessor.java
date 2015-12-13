/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 ******************************************************************************/
package com.sample.car;

import static com.sample.car.CarEdmProvider.ENTITY_SET_NAME_CARS;
import static com.sample.car.CarEdmProvider.ENTITY_SET_NAME_MANUFACTURERS;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmLiteralKind;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmSimpleType;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties.ODataEntityProviderPropertiesBuilder;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataNotFoundException;
import org.apache.olingo.odata2.api.exception.ODataNotImplementedException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;
import org.apache.olingo.odata2.api.uri.KeyPredicate;
import org.apache.olingo.odata2.api.uri.info.DeleteUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetCountUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntityUriInfo;
import org.apache.olingo.odata2.api.uri.info.PostUriInfo;

public class CarODataSingleProcessor extends ODataSingleProcessor {

	private final CarDataStore dataStore;

	public CarODataSingleProcessor() {
		dataStore = CarDataStore.INSTANCE;
	}

	@Override
	public ODataResponse readEntitySet(final GetEntitySetUriInfo uriInfo, final String contentType)
			throws ODataException {

		EdmEntitySet entitySet;

		if (uriInfo.getNavigationSegments().size() == 0) {
			entitySet = uriInfo.getStartEntitySet();

			if (ENTITY_SET_NAME_CARS.equals(entitySet.getName())) {
				return EntityProvider.writeFeed(contentType, entitySet, dataStore.getCars(),
						EntityProviderWriteProperties.serviceRoot(getContext().getPathInfo().getServiceRoot()).build());
			} else if (ENTITY_SET_NAME_MANUFACTURERS.equals(entitySet.getName())) {
				return EntityProvider.writeFeed(contentType, entitySet, dataStore.getManufacturers(),
						EntityProviderWriteProperties.serviceRoot(getContext().getPathInfo().getServiceRoot()).build());
			}

			throw new ODataNotFoundException(ODataNotFoundException.ENTITY);

		} else if (uriInfo.getNavigationSegments().size() == 1) {
			// navigation first level, simplified example for illustration
			// purposes only
			entitySet = uriInfo.getTargetEntitySet();

			if (ENTITY_SET_NAME_CARS.equals(entitySet.getName())) {
				int manufacturerKey = getKeyValue(uriInfo.getKeyPredicates().get(0));

				List<Map<String, Object>> cars = new ArrayList<Map<String, Object>>();
				cars.addAll(dataStore.getCarsFor(manufacturerKey));

				return EntityProvider.writeFeed(contentType, entitySet, cars,
						EntityProviderWriteProperties.serviceRoot(getContext().getPathInfo().getServiceRoot()).build());
			}

			throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
		}

		throw new ODataNotImplementedException();
	}

	@Override
	public ODataResponse readEntity(final GetEntityUriInfo uriInfo, final String contentType) throws ODataException {

		if (uriInfo.getNavigationSegments().size() == 0) {
			EdmEntitySet entitySet = uriInfo.getStartEntitySet();

			if (ENTITY_SET_NAME_CARS.equals(entitySet.getName())) {
				int id = getKeyValue(uriInfo.getKeyPredicates().get(0));
				Map<String, Object> data = dataStore.getCar(id);

				if (data != null) {
					URI serviceRoot = getContext().getPathInfo().getServiceRoot();
					ODataEntityProviderPropertiesBuilder propertiesBuilder = EntityProviderWriteProperties
							.serviceRoot(serviceRoot);

					return EntityProvider.writeEntry(contentType, entitySet, data, propertiesBuilder.build());
				}
			} else if (ENTITY_SET_NAME_MANUFACTURERS.equals(entitySet.getName())) {
				int id = getKeyValue(uriInfo.getKeyPredicates().get(0));
				Map<String, Object> data = dataStore.getManufacturer(id);

				if (data != null) {
					URI serviceRoot = getContext().getPathInfo().getServiceRoot();
					ODataEntityProviderPropertiesBuilder propertiesBuilder = EntityProviderWriteProperties
							.serviceRoot(serviceRoot);

					return EntityProvider.writeEntry(contentType, entitySet, data, propertiesBuilder.build());
				}
			}

			throw new ODataNotFoundException(ODataNotFoundException.ENTITY);

		} else if (uriInfo.getNavigationSegments().size() == 1) {
			// navigation first level, simplified example for illustration
			// purposes only
			EdmEntitySet entitySet = uriInfo.getTargetEntitySet();

			Map<String, Object> data = null;

			if (ENTITY_SET_NAME_MANUFACTURERS.equals(entitySet.getName())) {
				int carKey = getKeyValue(uriInfo.getKeyPredicates().get(0));
				data = dataStore.getManufacturerFor(carKey);
			}

			if (data != null) {
				return EntityProvider.writeEntry(contentType, uriInfo.getTargetEntitySet(), data,
						EntityProviderWriteProperties.serviceRoot(getContext().getPathInfo().getServiceRoot()).build());
			}

			throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
		}

		throw new ODataNotImplementedException();
	}

	@Override
	public ODataResponse countEntitySet(final GetEntitySetCountUriInfo uriInfo, final String contentType)
			throws ODataException {
		throw new ODataNotImplementedException();
	}

	@Override
	public ODataResponse createEntity(PostUriInfo uriInfo, InputStream content, String requestContentType,
			String contentType) throws ODataException {
		// No support for creating and linking a new entry
		if (uriInfo.getNavigationSegments().size() > 0) {
			throw new ODataNotImplementedException();
		}

		// No support for media resources
		if (uriInfo.getStartEntitySet().getEntityType().hasStream()) {
			throw new ODataNotImplementedException();
		}

		EntityProviderReadProperties properties = EntityProviderReadProperties.init().mergeSemantic(false).build();

		// ExceptionMapper handles and exceptions
		ODataEntry entry = EntityProvider.readEntry(
				requestContentType, 
				uriInfo.getStartEntitySet(), 
				content,
				properties);

		Map<String, Object> data = entry.getProperties();

		// TODO: Note no support for creation of anything other than a car currently
		String id = dataStore.addCar(data);
		data.put("Id", Long.valueOf(id));
		data.put("Updated", Calendar.getInstance(TimeZone.getTimeZone("GMT")));

		// serialize the entry, Location header is set by OData Library
		return EntityProvider.writeEntry(contentType, uriInfo.getStartEntitySet(), data,
				EntityProviderWriteProperties.serviceRoot(getContext().getPathInfo().getServiceRoot()).build());
	}
	
	@Override
	public ODataResponse deleteEntity(DeleteUriInfo uriInfo, String contentType) throws ODataException {
		throw new ODataNotImplementedException();
	}
	
	private int getKeyValue(final KeyPredicate key) throws ODataException {
		EdmProperty property = key.getProperty();
		EdmSimpleType type = (EdmSimpleType) property.getType();
		return type.valueOfString(key.getLiteral(), EdmLiteralKind.DEFAULT, property.getFacets(), Integer.class);
	}
}
