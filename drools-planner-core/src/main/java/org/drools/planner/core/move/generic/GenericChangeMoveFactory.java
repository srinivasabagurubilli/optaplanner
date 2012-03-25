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
import java.util.List;

import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.localsearch.LocalSearchSolverPhaseScope;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.move.factory.CachedMoveFactory;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solution.director.SolutionDirector;

public class GenericChangeMoveFactory extends CachedMoveFactory {

    private SolutionDescriptor solutionDescriptor;
    private SolutionDirector solutionDirector;

    @Override
    public void phaseStarted(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        solutionDescriptor = localSearchSolverPhaseScope.getSolutionDescriptor();
        solutionDirector = localSearchSolverPhaseScope.getSolutionDirector();
        super.phaseStarted(localSearchSolverPhaseScope);
    }

    @Override
    public List<Move> createCachedMoveList(Solution solution) {
        List<Move> moveList = new ArrayList<Move>();
        for (Object entity : solutionDescriptor.getPlanningEntityList(solution)) {
            PlanningEntityDescriptor entityDescriptor = solutionDescriptor.getPlanningEntityDescriptor(
                    entity.getClass());
            for (PlanningVariableDescriptor variableDescriptor : entityDescriptor.getPlanningVariableDescriptors()) {
                if (variableDescriptor.isChained()) {
                    throw new IllegalStateException("The planningEntityClass ("
                            + entityDescriptor.getPlanningEntityClass()
                            + ")'s planningVariableDescriptor (" + variableDescriptor.getVariablePropertyName()
                            + ") is chained and can therefor not use the moveFactory (" + this.getClass() + ").");
                }
                for (Object toPlanningValue : variableDescriptor.extractPlanningValues(
                        solutionDirector.getWorkingSolution(), entity)) {
                    moveList.add(new GenericChangeMove(entity, variableDescriptor, toPlanningValue));
                }
            }
        }
        return moveList;
    }

    @Override
    public void phaseEnded(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        super.phaseEnded(localSearchSolverPhaseScope);
        solutionDescriptor = null;
        solutionDirector = null;
    }

}
