package com.fm.service;

import POJO.DataList;

import java.io.IOException;

public interface CategoryService {

    DataList<String> getCategoryAndBrand(String categoryName) throws IOException;

    DataList<String> getCategory() throws IOException;
}
