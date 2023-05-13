package com.onix.worldtour.util;

import com.onix.worldtour.model.CostumeType;
import org.springframework.core.convert.converter.Converter;

public class StringToCostumeTypeConverter implements Converter<String, CostumeType> {

    @Override
    public CostumeType convert(String source) {
        return CostumeType.valueOf(source.toUpperCase());
    }
}
