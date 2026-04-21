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
        Clickable itLang();

        @XPath("//*[@id=\"lang-menu\"]/div[3]/ul/li[2]")
        Clickable enLang();

        @XPath("//*[@id=\"lang-menu\"]/div[3]/ul/li[3]")
        Clickable frLang();

        @XPath("//*[@id=\"lang-menu\"]/div[3]/ul/li[4]")
        Clickable deLang();

        @XPath("//*[@id=\"lang-menu\"]/div[3]/ul/li[5]")
        Clickable slLang();

        default void changeLanguage(String language) {
            languages().click();
            switch (language.toLowerCase()) {
                case "italiano" -> itLang().click();
                case "inglese" -> enLang().click();
                case "francese" -> frLang().click();
                case "tedesco" -> deLang().click();
                case "sloveno" -> slLang().click();
                default -> throw new IllegalArgumentException("Unknown language: " + language);
            }
        }
    }

    LanguageSelector languageSelector();

    default void changeLanguage(String language) {
        languageSelector().changeLanguage(language);
    }
}
