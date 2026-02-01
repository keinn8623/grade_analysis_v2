package com.keinn.grade_analysis_v2.common;


import io.micrometer.common.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public class AssignValuesToObject {

    public <T> T assignValues(List<?> list, Class<T> clazz) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = clazz.getDeclaredFields();

            int listIndex = 0; // 用于跟踪列表索引

            for (Field field : fields) {
                // 跳过 static final 字段
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                    continue;
                }

                // 跳过 GradeExcelId 字段
                if ("GradeExcelId".equals(field.getName())) {
                    continue;
                }

                // 确保列表中有足够的元素
                if (listIndex >= list.size()) {
                    break;
                }

                Object value = list.get(listIndex);

                field.setAccessible(true);

                // 类型转换处理
                Object convertedValue = convertValueIfNeeded(value, field.getType());
                field.set(instance, convertedValue);

                listIndex++;
            }

            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object convertValueIfNeeded(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        // 如果类型已经匹配，直接返回
        if (targetType.isAssignableFrom(value.getClass())) {
            return value;
        }

        // 执行类型转换
        String stringValue = value.toString();

        if (targetType == Long.class || targetType == long.class) {
            if (stringValue.isEmpty()) {
                return null;
            }
            // 检查是否为纯数字字符串
            if (isNumeric(stringValue)) {
                try {
                    return Long.parseLong(stringValue);
                } catch (NumberFormatException e) {
                    // 如果解析失败，返回null或默认值
                    return null;
                }
            } else {
                // 非数字字符串，返回null
                return null;
            }
        } else if (targetType == Integer.class || targetType == int.class) {
            if (stringValue.isEmpty()) {
                return null;
            }
            if (isNumeric(stringValue)) {
                try {
                    return Integer.parseInt(stringValue);
                } catch (NumberFormatException e) {
                    return null;
                }
            } else {
                return null;
            }
        } else if (targetType == Double.class || targetType == double.class) {
            if (stringValue.isEmpty()) {
                return null;
            }
            try {
                return Double.parseDouble(stringValue);
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (targetType == Float.class || targetType == float.class) {
            if (stringValue.isEmpty()) {
                return null;
            }
            try {
                return Float.parseFloat(stringValue);
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            if (stringValue.isEmpty()) {
                return null;
            }
            return Boolean.parseBoolean(stringValue);
        } else if (targetType == String.class) {
            return stringValue;
        }

        // 默认返回原值
        return value;
    }

    // 辅助方法：检查字符串是否为数字
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        // 检查是否包含非数字字符
        return str.matches("-?\\d+(\\.\\d+)?");
    }

}
