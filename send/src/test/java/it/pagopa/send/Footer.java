package it.pagopa.send;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;

public interface Footer extends Component {

    @XPath("//*[@id=\"root\"]/div[1]/footer/div[1]/div/div/div/a[1]")
    Clickable privacyPolicy();

    @XPath("//*[@id=\"root\"]/div[1]/footer/div[1]/div/div/div/a[2]")
    Clickable termAndConditions();

    @XPath("//*[@id=\"root\"]/div[1]/footer/div[1]/div/div/div/a[3]")
    Clickable sercqStatement();

    @XPath("//*[@id=\"root\"]/div[1]/footer/div[1]/div/div/div/a[4]")
    Clickable accessibility();

    interface LanguageSelector extends Component {
        @XPath("//*[@id=\"lang-menu-button\"]/span/span")
        Clickable languages();

        @XPath("//*[@id=\"lang-menu\"]/div[3]/ul/li[1]")
        Clickable it_lang();

        @XPath("//*[@id=\"lang-menu\"]/div[3]/ul/li[2]")
        Clickable en_lang();

        @XPath("//*[@id=\"lang-menu\"]/div[3]/ul/li[3]")
        Clickable fr_lang();

        @XPath("//*[@id=\"lang-menu\"]/div[3]/ul/li[4]")
        Clickable de_lang();

        @XPath("//*[@id=\"lang-menu\"]/div[3]/ul/li[5]")
        Clickable sl_lang();

        default void changeLanguage(String language) {
            languages().click();
            switch (language.toLowerCase()) {
                case "italiano" -> it_lang().click();
                case "inglese" -> en_lang().click();
                case "francese" -> fr_lang().click();
                case "tedesco" -> de_lang().click();
                case "sloveno" -> sl_lang().click();
                default -> throw new IllegalArgumentException("Unknown language: " + language);
            }
        }
    }

    LanguageSelector languageSelector();

    default void changeLanguage(String language) {
        languageSelector().changeLanguage(language);
    }
}
