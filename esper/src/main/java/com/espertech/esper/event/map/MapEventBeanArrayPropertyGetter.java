/*
 ***************************************************************************************
 *  Copyright (C) 2006 EsperTech, Inc. All rights reserved.                            *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 ***************************************************************************************
 */
package com.espertech.esper.event.map;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.PropertyAccessException;
import com.espertech.esper.codegen.core.CodegenContext;
import com.espertech.esper.codegen.model.expression.CodegenExpression;
import com.espertech.esper.event.BaseNestableEventUtil;

import java.util.Map;

import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.*;

/**
 * Returns the event bean or the underlying array.
 */
public class MapEventBeanArrayPropertyGetter implements MapEventPropertyGetter {
    private final String propertyName;
    private final Class underlyingType;

    /**
     * Ctor.
     *
     * @param propertyName   property to get
     * @param underlyingType type of property
     */
    public MapEventBeanArrayPropertyGetter(String propertyName, Class underlyingType) {
        this.propertyName = propertyName;
        this.underlyingType = underlyingType;
    }

    public Object getMap(Map<String, Object> map) throws PropertyAccessException {
        Object mapValue = map.get(propertyName);
        return BaseNestableEventUtil.getArrayPropertyAsUnderlyingsArray(underlyingType, (EventBean[]) mapValue);
    }

    private String getMapCodegen(CodegenContext context) {
        return context.addMethod(Object.class, Map.class, "map", this.getClass())
                .declareVar(Object.class, "mapValue", exprDotMethod(ref("map"), "get", constant(propertyName)))
                .methodReturn(localMethod(BaseNestableEventUtil.getArrayPropertyAsUnderlyingsArrayCodegen(underlyingType, context), cast(EventBean[].class, ref("mapValue"))));
    }

    public boolean isMapExistsProperty(Map<String, Object> map) {
        return true; // Property exists as the property is not dynamic (unchecked)
    }

    public Object get(EventBean obj) {
        return getMap(BaseNestableEventUtil.checkedCastUnderlyingMap(obj));
    }

    public boolean isExistsProperty(EventBean eventBean) {
        return true; // Property exists as the property is not dynamic (unchecked)
    }

    public Object getFragment(EventBean obj) {
        Map<String, Object> map = BaseNestableEventUtil.checkedCastUnderlyingMap(obj);
        return map.get(propertyName);
    }

    public CodegenExpression codegenEventBeanGet(CodegenExpression beanExpression, CodegenContext context) {
        return codegenUnderlyingGet(castUnderlying(Map.class, beanExpression), context);
    }

    public CodegenExpression codegenEventBeanExists(CodegenExpression beanExpression, CodegenContext context) {
        return constantTrue();
    }

    public CodegenExpression codegenEventBeanFragment(CodegenExpression beanExpression, CodegenContext context) {
        return codegenUnderlyingFragment(castUnderlying(Map.class, beanExpression), context);
    }

    public CodegenExpression codegenUnderlyingGet(CodegenExpression underlyingExpression, CodegenContext context) {
        return localMethod(getMapCodegen(context), underlyingExpression);
    }

    public CodegenExpression codegenUnderlyingExists(CodegenExpression underlyingExpression, CodegenContext context) {
        return constantTrue();
    }

    public CodegenExpression codegenUnderlyingFragment(CodegenExpression underlyingExpression, CodegenContext context) {
        return exprDotMethod(underlyingExpression, "get", constant(propertyName));
    }
}
