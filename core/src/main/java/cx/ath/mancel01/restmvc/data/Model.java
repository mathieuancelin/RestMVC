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

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 *
 * @author Mathieu ANCELIN
 */
@MappedSuperclass
public abstract class Model<T> {
    
    @Transient
    private transient DataHelper helper;

    @Transient
    private transient Class clazz;

    public Model() {
        if (helper == null) {
            clazz = getClass();
            helper = DataHelper.forType(clazz);
        }
    }

    public T delete() {
        return (T) helper.delete(this);
    }

    public T refresh() {
        return (T) helper.refresh(this);
    }

    public T save() {
        return (T) helper.save(this);
    }
}
