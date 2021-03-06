/*
 * myLazyClock
 *
 * Copyright (C) 2014
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.myLazyClock.travelApi;

import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created on 28/10/14.
 *
 * @author dralagen
 */
public class TravelFactory  implements Iterable<TravelStrategy> {

    private static TravelFactory ourInstance = null;

    /**
     * Accessor of instance of TravelFactory
     *
     * @return the TravelFactory of this computer
     */
    public static TravelFactory getInstance() {
        if (ourInstance == null) {
            ourInstance = new TravelFactory();
        }
        return ourInstance;
    }

    /**
     * List all module who implement {@link TravelStrategy}
     */
    private Map<TravelId, TravelStrategy> allModules;

    /**
     * Search all module who implement {@link TravelStrategy}
     * and put in allModule
     */
    private TravelFactory() {
        allModules = new HashMap<>();

        Reflections reflections = new Reflections("org.myLazyClock.travelApi");
        Set<Class<? extends TravelStrategy>> modules = reflections.getSubTypesOf(TravelStrategy.class);

        for (Class<? extends TravelStrategy> aModule : modules) {

            try {
                TravelStrategy instance = aModule.newInstance();

                TravelId id = instance.getId();

                if (allModules.containsKey(id)) {
                    throw new IllegalStateException(
                            "Strategy [" +
                                    instance.getName() + "] with id " + id +
                                    ", can not be used since the id is already used " +
                                    "by strategy [" + allModules.get(id).getName() + "] ");
                }
                allModules.put(id, instance);

            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public Iterator<TravelStrategy> iterator() {
        return allModules.values().iterator();
    }

    /**
     * Select the {@link TravelStrategy} with id
     *
     * @param id Identifier of the {@link TravelStrategy}
     * @return the {@link TravelStrategy} if find,
     * @throws NullPointerException
     */
    public TravelStrategy get(TravelId id) throws NullPointerException {
        TravelStrategy strategy = allModules.get(id);
        if (strategy == null) {
            throw new NullPointerException();
        }
        return strategy;
    }
}
