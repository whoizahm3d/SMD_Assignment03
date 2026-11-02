package com.example.smd_assignment03;

import com.example.smd_assignment03.models.CatalogItem;

import java.util.ArrayList;

public class AttachmentCatalog {

    public static ArrayList<CatalogItem> getItems() {
        ArrayList<CatalogItem> list = new ArrayList<>();
        list.add(new CatalogItem("cat_1", "sports-projectv2.png", "IMAGE", R.drawable.sports_project_v2, "PNG"));
        list.add(new CatalogItem("cat_2", "mount asset.png", "IMAGE", R.drawable.mount_asset, "PNG"));
        list.add(new CatalogItem("cat_3", "briefing from client.txt", "TEXT", R.raw.briefing_from_client, "TXT"));
        list.add(new CatalogItem("cat_4", "brand-colors.png", "IMAGE", R.drawable.brand_colors, "PNG"));
        list.add(new CatalogItem("cat_5", "hero-banner.jpg", "IMAGE", R.drawable.hero_banner, "JPG"));
        list.add(new CatalogItem("cat_6", "meeting-notes.txt", "TEXT", R.raw.meeting_notes, "TXT"));
        list.add(new CatalogItem("cat_7", "api-spec.txt", "TEXT", R.raw.api_spec, "TXT"));
        return list;
    }

    public static CatalogItem findById(String id) {
        for (CatalogItem item : getItems()) {
            if (item.getId().equals(id))
            {
                return item;
            }
        }
        return null;
    }
}
