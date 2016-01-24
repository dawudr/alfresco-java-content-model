package org.amnesty.aidoc.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

@Deprecated
public interface AidocServiceClient {

    public Document getAssetMetadata(String year, String aiClass,
            String docnum, String lang) throws Exception;

    public String createAsset(String aiClass, String type, String title,
            String year, String docnum, String publishDate,
            String securityClass, List<String> categories,
            List<String> secondaryCategories) throws Exception;

    public void createType(String aiIndex, String edition, String type,
            Map<String, String> properties) throws Exception;

    public void updateAsset(String aiIndex, String title,
            List<String> categories) throws Exception;

    public void updateAsset(String aiIndex, String title, String securityClass,
            List<String> categories, String invalidated, String validityNotes,
            List<String> secondaryCategories) throws Exception;

    public void updateType(String aiIndex, String type, String lang,
            String mimetype, Map<String, String> properties) throws Exception;

    public String getTicket(String username, String password) throws Exception;

    public String getTicket() throws Exception;

    public String uploadFile(File file) throws Exception;

    public boolean deleteYearFolder(String year);

    public boolean deleteAiIndexFolder(String year, String aiClass,
            String docnum);

}
