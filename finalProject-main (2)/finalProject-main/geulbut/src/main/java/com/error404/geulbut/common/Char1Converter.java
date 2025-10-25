package com.error404.geulbut.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class Char1Converter implements AttributeConverter<Character, String> {
    @Override
    public String convertToDatabaseColumn(Character attribute) {
        return attribute == null ? null : String.valueOf(Character.toUpperCase(attribute));
    }
    @Override
    public Character convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return null;
        return Character.toUpperCase(dbData.charAt(0));
    }
}
