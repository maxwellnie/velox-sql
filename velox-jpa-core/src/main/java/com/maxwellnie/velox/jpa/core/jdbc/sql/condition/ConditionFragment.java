package com.maxwellnie.velox.jpa.core.jdbc.sql.condition;

import com.maxwellnie.velox.jpa.core.enums.RelationShip;
import com.maxwellnie.velox.jpa.core.jdbc.sql.SqlFragment;

public abstract class ConditionFragment implements SqlFragment {
    private RelationShip relationShip = RelationShip.AND;

    public RelationShip getRelationShip() {
        return relationShip;
    }

    public void setRelationShip(RelationShip relationShip) {
        this.relationShip = relationShip;
    }
}
