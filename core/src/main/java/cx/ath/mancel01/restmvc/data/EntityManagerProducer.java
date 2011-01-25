/*
 *  Copyright 2010 Mathieu ANCELIN.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package cx.ath.mancel01.restmvc.data;

import cx.ath.mancel01.restmvc.FrameworkFilter;
import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;

/**
 *
 * @author Mathieu ANCELIN
 */
@RequestScoped
public class EntityManagerProducer implements Serializable {

    private static final long serialVersionUID = -5267593171036179836L;

    @Produces
    public EntityManager create() {
        return FrameworkFilter.currentEm.get();
    }
}
