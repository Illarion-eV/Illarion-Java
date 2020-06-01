/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.easyquest.quest;

import java.io.Serializable;

@SuppressWarnings("serial")
public final class IntegerRelation implements Serializable {

    private Relation relation;
    private long integer;

    public IntegerRelation() {
        setRelation(new Relation());
        setInteger(0);
    }

    public IntegerRelation(Relation relation, long integer) {
        setRelation(relation);
        setInteger(integer);
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    public Relation getRelation() {
        return relation;
    }

    public void setInteger(long integer) {
        this.integer = integer;
    }

    public long getInteger() {
        return integer;
    }
}
