package com.benjtissot.sellingmugs

import io.ktor.http.*

class Const {
    enum class UserType(var type: String) {
        CLIENT("CLIENT"),
        ADMIN("ADMIN"),;

        override fun toString(): String {
            return type
        }
    }
    enum class StableDiffusionImageType(var type: String) {
        REALISTIC("REALISTIC"),
        CARTOON_ILLUSTRATION("CARTOON_ILLUSTRATION"),
        GEOMETRIC("GEOMETRIC"),
        ;

        override fun toString(): String {
            return type
        }
    }
    enum class ClickType(var type: String) {
        HOME_NAV("HOME_NAV"),
        PROFILE_NAV("PROFILE_NAV"),
        CART_NAV("CART_NAV"),
        CART_CHECKOUT("CART_CHECKOUT"),
        LOGIN("LOGIN"),
        REGISTER("REGISTER"),
        LOGOUT("LOGOUT"),
        USER_INFO_TAB("USER_INFO_TAB"),
        USER_INFO_ORDER_TAB("USER_INFO_ORDER_TAB"),
        SHOW_ORDER_DETAILS("SHOW_ORDER_DETAILS"),
        CANCEL_ORDER("CANCEL_ORDER"),
        ADD_MUG_TO_CART("ADD_MUG_TO_CART"),
        TEST_PAY("TEST_PAY"),
        REAL_PAY_POPUP("REAL_PAY_POPUP"),
        CLOSE_WAITING_FOR_PAYMENT("CLOSE_WAITING_FOR_PAYMENT"),
        CONFIRM_REAL_PAY("CONFIRM_REAL_PAY"),
        CUSTOM_MUG_OPEN_PAGE("CUSTOM_MUG_OPEN_PAGE"),
        CUSTOM_MUG_UPLOAD_IMAGE("CUSTOM_MUG_UPLOAD_IMAGE"),
        CUSTOM_MUG_REFRESH_PREVIEW("CUSTOM_MUG_REFRESH_PREVIEW"),
        CUSTOM_MUG_ADD_TO_CART("CUSTOM_MUG_ADD_TO_CART"),
        CART_SEE_AVAILABLE_MUGS("CART_SEE_AVAILABLE_MUGS"),
        CART_LOAD_SAVED_CART("CART_LOAD_SAVED_CART"),
        CART_SAVE_TO_USER("CART_SAVE_TO_USER"),
        GENERATE_MUGS_TAB("GENERATE_MUGS_TAB"),
        GENERATE_CATEGORIES_TAB("GENERATE_CATEGORIES_TAB"),
        GENERATE_MUGS_BUTTON("GENERATE_MUGS_BUTTON"),
        GENERATE_CUSTOM_DESIGN_BUTTON("GENERATE_CUSTOM_DESIGN_BUTTON"),
        GENERATE_CATEGORIES_BUTTON("GENERATE_CATEGORIES_BUTTON"),
        ;

        override fun toString(): String {
            return type
        }
    }

    enum class ColorCode(var colourCode: String) {
        BLUE("#1976d2"),
        LIGHT_BLUE("#e0ecf4"),
        DARK_BLUE("#0088d0"),
        BACKGROUND_GREY("#f7f7f7"),
        BACKGROUND_GREY_DARK("#f0f0f0"),
        BACKGROUND_GREY_DARKER("#e0e0e0"),
        BACKGROUND_GREY_EVEN_DARKER("#d0d0d0"),
        BACKGROUND_GREY_DARKEST("#c0c0c0"),
        RED("#ff0b00"),
        ;
        fun code(): String {
            return colourCode
        }
    }

    companion object {
        // Environment Variable names
        const val PORT_STRING = "PORT"
        const val HOST_STRING = "HOST"
        const val STRIPE_WEBHOOK_SECRET_TEST_STRING = "STRIPE_WEBHOOK_SECRET_TEST"
        const val STRIPE_WEBHOOK_SECRET_REAL_STRING = "STRIPE_WEBHOOK_SECRET_REAL"
        const val STRIPE_API_KEY_TEST_STRING = "STRIPE_API_KEY_TEST"
        const val STRIPE_API_KEY_REAL_STRING = "STRIPE_API_KEY_REAL"
        const val PRINTIFY_STORE_ID_STRING = "PRINTIFY_STORE_ID"
        const val MONGODB_URI_STRING = "MONGODB_URI"
        const val MONGODB_DBNAME_STRING = "MONGODB_DBNAME"
        const val IS_PRODUCTION_STRING = "ORG_GRADLE_PROJECT_isProduction"
        const val OPENAI_API_KEY = "OPENAI_API_KEY"
        const val STABLE_DIFFUSION_API_KEY = "STABLE_DIFFUSION_API_KEY"

        // Filter Order
        const val ORDER_FILTER_ALL = "All"
        const val ORDER_FILTER_SIX_MONTHS = "< 6 months"
        const val ORDER_FILTER_THREE_MONTHS = "< 3 months"

        // Front-end URL params
        const val mugPrintifyId = "mugPrintifyId"


        // Query params
        const val updateType = "updateType"
        const val titleDesc = "titleDesc"
        const val image = "image"
        const val cartId = "cartId"
        const val orderId = "orderId"
        const val deltaQuantity = "deltaQuantity"

        // Call params
        const val id = "id"
        const val path = "path"
        const val productId = "productId"
        const val clickDataId = "clickDataId"
        const val clickType = "clickType"
        const val localOrderId = "localOrderId"
        const val userId = "userId"
        const val public = "public"
        const val printifyId = "printifyId"
        const val mugId = "mugId"
        const val categories = "categories"
        const val count = "count"
        const val pageNumber = "pageNumber"
        const val orderByViews = "orderByViews"
        const val param = "param"
        const val productInfo = "product_info"

        // Stripe params
        const val payment_intent = "payment_intent"

        // Mug Description Prefill text
        const val mugTitlePrefill = "Mug Title"
        const val mugDescriptionPrefill = "This 11oz mug is made of brilliant white ceramic material with AAA+ ORCA coating, making them excellent for printing vibrant colors. They are easy to clean, microwave-safe, and the orca coating mugs can withstand up to 3000 cycles in the dishwasher."

        // Mug Subject Prefill text
        const val mugSubjectPrefill = "Urban Photography"

        // Hidden overflow card url
        const val maskUrl = "url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAA5JREFUeNpiYGBgAAgwAAAEAAGbA+oJAAAAAElFTkSuQmCC);"

        const val mugListDisplayList = "list"
        const val mugListDisplayGrid = "grid"

        const val mugCategoryDefault = "Default"

        const val contactLinkedin = "https://www.linkedin.com/in/benjamin-tissot-49a1538b/"
        const val contactGitHub = "https://github.com/benjamintissotbenj/selling-mugs-project"

        const val disclaimerMessage = "This website is part of a MSc Project for Imperial College. " +
                "As such, you have the possibility to create a test order to help with the " +
                "research, or to checkout a real order that will result in a mug being delivered. " +
                "Please bear in mind that only addresses in England will be accepted for delivery."

        const val projectDescriptionMessage = "This website is not meant to be used for commercial purposes. " +
                "This is a project for a Master's Thesis for a MSc Computing at Imperial College London. " +
                "The goal of this project is to create a fully automated e-commerce platform for selling mugs. " +
                "This includes the possibility to customise mugs, to order mugs with existing designs and other things. " +
                "This website gives you the possibility to create fake orders as well as real orders, since the idea was " +
                "that people could use the website without actually spending money. "

        fun getPromptResponseStructure(numberOfVariations: Int) : String {
            return "Structure of a Variation object : { name, parameters, narrative }. " +
                    "The structure of the response should be a JSON object containing the different " +
                    "variations, with a unique field called variations containing the Variation objects . Each object has " +
                    "five fields, one that contains the name you give to this variation (under the name name), " +
                    "a second that contains one string with all the variation parameters (under the name parameters) " +
                    "a third that contains the narrative (under the name narrative), " +
                    "a fourth one that contains a brief of the narrative (under the name description) " +
                    "and a fifth that contains an appropriate negative prompt for this image (under the name negative_prompt), " +
                    "made only of key words of things we do not want appearing in the final image. " +
                    "Please do not use \"Variation 1\" or similar names for the variation names. Find something original. " +
                    "Based on the above structures, " +
                    "create $numberOfVariations different variations for the subject. Then, write a detailed narrative of about 400 characters " +
                    "for each variation and store it in the JSON under the name prompt. Write a short summary of the narrative in around 200 characters" +
                    "and store it in the JSON under the name description. The response should contain the JSON " +
                    "object and only the JSON object, so that the response can be parsed. Subject of the prompt is: "
        }

        fun getRealisticStructure() : String {
            return "Structure of a Stable Diffusion prompt : (subject of the image), (camera type), (camera lens type), (time of day), (style of photography), (Realism Level), (Lighting). "
        }

        fun getGeometricStructure() : String {
            return "Structure of a Stable Diffusion prompt : (subject of the image), (most appropriate shapes for the subject), (symmetry), (computer generated or human drawn), (Realism Level), (Lighting). "
        }
        fun getCartoonStructure() : String {
            return "Structure of a Stable Diffusion prompt : (subject of the image), (drawing type), (illustration style), (time of day), (known artist style), (Realism Level), (Lighting). "
        }

        /**
         * Creates a chatGPT prompt to create categories
         * @param numberOfCategories the number of categories to be created
         * @param categoriesToExclude the different categories to be excluded from the generated list of categories
         * @return a [String] containing the prompt
         */
        fun getGenerateCategoriesPrompt(numberOfCategories: Int, categoriesToExclude: String) : String {
            return "Create a JSON file with a list called \"categories\" of $numberOfCategories different categories for mug designs. " +
                    "Any category name should be 1 to 3 words long. For each category object in the list, indicate its name (field name \"category\") and " +
                    "which style (field name \"style\") would best be suited between REALISTIC, GEOMETRIC and CARTOON_ILLUSTRATION." +
                    if (categoriesToExclude.isNotBlank()) "You should not include any category in this list : $categoriesToExclude. " else ""
        }

        val HttpStatusCode_OpenAIUnavailable = HttpStatusCode(80, "OpenAI Server is unavailable, please try later")
        val HttpStatusCode_ImageUploadFail = HttpStatusCode(91, "Image Upload failed")
        val HttpStatusCode_ProductCreationFailed = HttpStatusCode(92, "Product Creation Failed")
        val HttpStatusCode_ProductPublicationFailed = HttpStatusCode(93, "Product Publication Failed")
    }
}