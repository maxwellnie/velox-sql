package com.maxwellnie.vleox.jpa.core.jdbc.sql.condition;

import com.maxwellnie.vleox.jpa.core.enums.RelationShip;
import com.maxwellnie.vleox.jpa.core.jdbc.sql.SqlFragment;

public abstract class ConditionFragment implements SqlFragment {
    private RelationShip relationShip = RelationShip.AND;

    public RelationShip getRelationShip() {
        return relationShip;
    }

    public void setRelationShip(RelationShip relationShip) {
        this.relationShip = relationShip;
    }
}
