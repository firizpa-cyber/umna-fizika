package uz.fizika.core.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * «Мягкий неон» — цветовая палитра приложения.
 * Подобрана для минимальной нагрузки на зрение при длительном чтении.
 */
object NeonColors {

    // === Фоны ===
    val Background       = Color(0xFF0D0F1A)   // Почти чёрный с синим тоном
    val Surface          = Color(0xFF151828)   // Поверхность карточек
    val SurfaceVariant   = Color(0xFF1E2235)   // Elevated поверхность
    val SurfaceContainer = Color(0xFF252A40)   // Контейнеры (диалоги, bottom sheets)

    // === Основной акцент — Приглушённый фиолетово-синий ===
    val Primary          = Color(0xFF7B8CDE)   // Основной
    val PrimaryDim       = Color(0xFF5566C8)   // Затемнённый (pressed)
    val PrimaryContainer = Color(0xFF1E2452)   // Контейнер
    val OnPrimary        = Color(0xFF0D0F1A)
    val OnPrimaryContainer = Color(0xFFADB8F0)

    // === Вторичный акцент — Мятный ===
    val Secondary          = Color(0xFF4FC3A1) // Правильные ответы, прогресс
    val SecondaryDim       = Color(0xFF35A888)
    val SecondaryContainer = Color(0xFF0F3D30)
    val OnSecondary        = Color(0xFF0D0F1A)
    val OnSecondaryContainer = Color(0xFF8EDFCB)

    // === Предупреждения — Тёплый оранжевый ===
    val Tertiary           = Color(0xFFE88C5A) // Таймер, ошибки, предупреждения
    val TertiaryDim        = Color(0xFFCC7040)
    val TertiaryContainer  = Color(0xFF3D2010)
    val OnTertiary         = Color(0xFF0D0F1A)

    // === Ошибки ===
    val Error          = Color(0xFFFF6B6B)
    val ErrorContainer = Color(0xFF3D1010)
    val OnError        = Color(0xFF0D0F1A)

    // === Текст ===
    val OnBackground     = Color(0xFFE8EAF6)  // Основной текст (не чисто белый)
    val OnSurface        = Color(0xFFCDD0E8)  // Текст на поверхности
    val OnSurfaceVariant = Color(0xFF7880A8)  // Вторичный/подсказочный текст
    val Outline          = Color(0xFF3A3F5C)  // Границы элементов
    val OutlineVariant   = Color(0xFF252840)  // Слабые границы

    // === Специальные эффекты ===
    val GlowPrimary   = Color(0x337B8CDE)    // Свечение (прозрачный основной)
    val GlowSecondary = Color(0x334FC3A1)    // Свечение (прозрачный вторичный)
    val GlowTertiary  = Color(0x33E88C5A)    // Свечение (прозрачный оранжевый)

    // === XP / Геймификация ===
    val Gold     = Color(0xFFFFD700)
    val Silver   = Color(0xFFC0C8D8)
    val Bronze   = Color(0xFFCD7F32)
    val XpGlow   = Color(0x55FFD700)
}
