/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.planner.core.move.generic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.WorkingMemory;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.move.Move;

public class GenericSwapMove implements Move {

    private final Collection<PlanningVariableDescriptor> planningVariableDescriptors;

    private final Object leftPlanningEntity;

    private final Object rightPlanningEntity;

    public GenericSwapMove(Collection<PlanningVariableDescriptor> planningVariableDescriptors,
            Object leftPlanningEntity, Object rightPlanningEntity) {
        this.planningVariableDescriptors = planningVariableDescriptors;
        this.leftPlanningEntity = leftPlanningEntity;
        this.rightPlanningEntity = rightPlanningEntity;
    }

    public boolean isMoveDoable(WorkingMemory workingMemory) {
        for (PlanningVariableDescriptor planningVariableDescriptor : planningVariableDescriptors) {
            Object leftValue = planningVariableDescriptor.getValue(leftPlanningEntity);
            Object rightValue = planningVariableDescriptor.getValue(rightPlanningEntity);
            if (!ObjectUtils.equals(leftValue, rightValue)) {
                return true;
            }
        }
        return false;
    }

    public Move createUndoMove(WorkingMemory workingMemory) {
        return new GenericSwapMove(planningVariableDescriptors,
                rightPlanningEntity, leftPlanningEntity);
    }

    public void doMove(WorkingMemory workingMemory) {
        for (PlanningVariableDescriptor planningVariableDescriptor : planningVariableDescriptors) {
            Object leftValue = planningVariableDescriptor.getValue(leftPlanningEntity);
            Object rightValue = planningVariableDescriptor.getValue(rightPlanningEntity);
            if (!ObjectUtils.equals(leftValue, rightValue)) {
                planningVariableDescriptor.setValue(leftPlanningEntity, rightValue);
                workingMemory.update(workingMemory.getFactHandle(leftPlanningEntity), leftPlanningEntity);
                planningVariableDescriptor.setValue(rightPlanningEntity, leftValue);
                workingMemory.update(workingMemory.getFactHandle(rightPlanningEntity), rightPlanningEntity);
            }
        }
    }
    public Collection<? extends Object> getPlanningEntities() {
        return Arrays.asList(leftPlanningEntity, rightPlanningEntity);
    }

    public Collection<? extends Object> getPlanningValues() {
        List<Object> values = new ArrayList<Object>(planningVariableDescriptors.size() * 2);
        for (PlanningVariableDescriptor planningVariableDescriptor : planningVariableDescriptors) {
            values.add(planningVariableDescriptor.getValue(leftPlanningEntity));
            values.add(planningVariableDescriptor.getValue(rightPlanningEntity));
        }
        return values;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof GenericSwapMove) {
            GenericSwapMove other = (GenericSwapMove) o;
            return new EqualsBuilder()
                    .append(leftPlanningEntity, other.leftPlanningEntity)
                    .append(rightPlanningEntity, other.rightPlanningEntity)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftPlanningEntity)
                .append(rightPlanningEntity)
                .toHashCode();
    }

    public String toString() {
        return leftPlanningEntity + " <=> " + rightPlanningEntity;
    }

}
