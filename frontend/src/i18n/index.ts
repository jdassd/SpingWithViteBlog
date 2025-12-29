import { createI18n } from 'vue-i18n'
import zhCN from './locales/zh-CN'
import en from './locales/en'

// Get saved language from localStorage or use default
const getDefaultLocale = (): string => {
    const saved = localStorage.getItem('locale')
    if (saved && ['zh-CN', 'en'].includes(saved)) {
        return saved
    }
    // Check browser language
    const browserLang = navigator.language
    if (browserLang.startsWith('zh')) {
        return 'zh-CN'
    }
    return 'zh-CN' // Default to Chinese
}

const i18n = createI18n({
    legacy: false, // Use Composition API mode
    locale: getDefaultLocale(),
    fallbackLocale: 'en',
    messages: {
        'zh-CN': zhCN,
        en: en,
    },
})

// Helper function to change locale
export const setLocale = (locale: string) => {
    if (['zh-CN', 'en'].includes(locale)) {
        i18n.global.locale.value = locale as 'zh-CN' | 'en'
        localStorage.setItem('locale', locale)
        document.documentElement.lang = locale === 'zh-CN' ? 'zh' : 'en'
    }
}

// Helper function to get current locale
export const getLocale = (): string => {
    return i18n.global.locale.value
}

// Language options for UI
export const languageOptions = [
    { value: 'zh-CN', label: '简体中文' },
    { value: 'en', label: 'English' },
]

export default i18n
