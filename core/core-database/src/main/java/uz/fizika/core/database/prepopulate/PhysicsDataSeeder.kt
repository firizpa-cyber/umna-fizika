package uz.fizika.core.database.prepopulate

import uz.fizika.core.database.AppDatabase
import uz.fizika.core.database.entities.*

/**
 * Засевает БД начальным набором физических формул и разделов.
 * Вызывается один раз при первом создании БД.
 */
object PhysicsDataSeeder {

    suspend fun seed(db: AppDatabase) {
        if (db.topicDao().count() > 0) return  // уже засеяно

        val topics = buildTopics()
        db.topicDao().insertTopics(topics)

        val formulas = buildFormulas()
        db.formulaDao().insertFormulas(formulas)

        val links = buildLinks()
        db.formulaDao().insertLinks(links)

        // Создаём профиль пользователя по умолчанию
        db.userProfileDao().upsertProfile(UserProfileEntity())
    }

    // ─── Разделы физики ───────────────────────────────────────────────────────
    private fun buildTopics() = listOf(
        TopicEntity("mechanics",       "Механика",           "⚙️", 0xFF7B8CDE),
        TopicEntity("thermodynamics",  "Термодинамика",      "🌡️", 0xFFE88C5A),
        TopicEntity("electrostatics",  "Электростатика",     "⚡", 0xFFFFD700),
        TopicEntity("electrodynamics", "Электродинамика",    "🔌", 0xFF4FC3A1),
        TopicEntity("optics",          "Оптика",             "🔭", 0xFF9B8CD8),
        TopicEntity("quantum",         "Квантовая физика",   "⚛️", 0xFF5BC8F0),
        TopicEntity("nuclear",         "Ядерная физика",     "☢️", 0xFFFF6B6B),
        TopicEntity("relativity",      "Теория относительности","🚀", 0xFFB8A0FF)
    )

    // ─── Формулы ──────────────────────────────────────────────────────────────
    private fun buildFormulas() = listOf(

        // === Механика ===
        FormulaEntity("f_newton_2",   "mechanics",       "Второй закон Ньютона",
            "F = ma",
            "Сила равна произведению массы тела на его ускорение",
            "Н = кг·м/с²",
            """{"F":"сила (Н)","m":"масса (кг)","a":"ускорение (м/с²)"}""",
            difficulty = 1),

        FormulaEntity("kinematics_v", "mechanics",       "Скорость равноускоренного движения",
            "v = v_0 + at",
            "Конечная скорость при равноускоренном движении",
            "м/с",
            """{"v":"скорость","v_0":"начальная скорость","a":"ускорение","t":"время"}""",
            difficulty = 1),

        FormulaEntity("kinematics_s", "mechanics",       "Перемещение при равноускоренном движении",
            "s = v_0 t + \\frac{at^2}{2}",
            "Перемещение тела при равноускоренном движении",
            "м",
            """{"s":"перемещение","v_0":"начальная скорость","a":"ускорение","t":"время"}""",
            difficulty = 2),

        FormulaEntity("energy_kinetic","mechanics",      "Кинетическая энергия",
            "E_k = \\frac{mv^2}{2}",
            "Кинетическая энергия движущегося тела",
            "Дж = кг·м²/с²",
            """{"E_k":"кинетическая энергия","m":"масса","v":"скорость"}""",
            difficulty = 1),

        FormulaEntity("energy_potential","mechanics",    "Потенциальная энергия (гравитация)",
            "E_p = mgh",
            "Потенциальная энергия тела в поле тяжести",
            "Дж",
            """{"E_p":"потенциальная энергия","m":"масса","g":"9.81 м/с²","h":"высота"}""",
            difficulty = 1),

        FormulaEntity("momentum",     "mechanics",       "Импульс тела",
            "p = mv",
            "Импульс — произведение массы на скорость",
            "кг·м/с",
            """{"p":"импульс","m":"масса","v":"скорость"}""",
            difficulty = 1),

        FormulaEntity("gravity",      "mechanics",       "Закон всемирного тяготения",
            "F = G \\frac{m_1 m_2}{r^2}",
            "Сила притяжения между двумя телами",
            "Н",
            """{"G":"6.674·10⁻¹¹","m_1":"масса 1","m_2":"масса 2","r":"расстояние"}""",
            difficulty = 3),

        // === Термодинамика ===
        FormulaEntity("ideal_gas",    "thermodynamics",  "Уравнение состояния идеального газа",
            "pV = nRT",
            "Связь давления, объёма и температуры идеального газа",
            "Па·м³ = моль·Дж/(моль·К)·К",
            """{"p":"давление","V":"объём","n":"кол-во молей","R":"8.314","T":"температура (К)"}""",
            difficulty = 2),

        FormulaEntity("first_law_thermo","thermodynamics","Первое начало термодинамики",
            "\\Delta U = Q - W",
            "Изменение внутренней энергии газа",
            "Дж",
            """{"ΔU":"изменение внутренней энергии","Q":"количество теплоты","W":"работа газа"}""",
            difficulty = 2),

        // === Электростатика ===
        FormulaEntity("coulomb",      "electrostatics",  "Закон Кулона",
            "F = k \\frac{q_1 q_2}{r^2}",
            "Сила взаимодействия двух точечных зарядов",
            "Н",
            """{"k":"9·10⁹","q_1":"заряд 1 (Кл)","q_2":"заряд 2 (Кл)","r":"расстояние (м)"}""",
            difficulty = 2),

        FormulaEntity("electric_field","electrostatics", "Напряжённость электрического поля",
            "E = \\frac{F}{q}",
            "Напряжённость поля в данной точке",
            "В/м = Н/Кл",
            """{"E":"напряжённость","F":"сила","q":"заряд"}""",
            difficulty = 2),

        // === Электродинамика ===
        FormulaEntity("ohm_law",      "electrodynamics", "Закон Ома",
            "I = \\frac{U}{R}",
            "Ток в цепи равен напряжению делённому на сопротивление",
            "А = В/Ом",
            """{"I":"ток (А)","U":"напряжение (В)","R":"сопротивление (Ом)"}""",
            difficulty = 1),

        FormulaEntity("power_electric","electrodynamics","Мощность электрического тока",
            "P = UI = I^2 R = \\frac{U^2}{R}",
            "Мощность, выделяемая на участке цепи",
            "Вт",
            """{"P":"мощность","U":"напряжение","I":"ток","R":"сопротивление"}""",
            difficulty = 2),

        // === Оптика ===
        FormulaEntity("snell_law",    "optics",          "Закон Снеллиуса (преломление)",
            "n_1 \\sin\\theta_1 = n_2 \\sin\\theta_2",
            "Закон преломления света на границе двух сред",
            "безразмерный",
            """{"n_1":"показатель преломления 1","n_2":"показатель преломления 2","θ_1":"угол падения","θ_2":"угол преломления"}""",
            difficulty = 3),

        FormulaEntity("thin_lens",    "optics",          "Формула тонкой линзы",
            "\\frac{1}{f} = \\frac{1}{d_o} + \\frac{1}{d_i}",
            "Связь фокусного расстояния с расстояниями до предмета и изображения",
            "м⁻¹",
            """{"f":"фокусное расстояние","d_o":"расстояние до предмета","d_i":"расстояние до изображения"}""",
            difficulty = 3),

        // === Квантовая физика ===
        FormulaEntity("photoeffect",  "quantum",         "Уравнение Эйнштейна для фотоэффекта",
            "h\\nu = A + \\frac{mv^2}{2}",
            "Энергия фотона расходуется на работу выхода и кинетическую энергию электрона",
            "Дж",
            """{"h":"6.626·10⁻³⁴","ν":"частота","A":"работа выхода","m":"масса электрона","v":"скорость электрона"}""",
            difficulty = 4),

        FormulaEntity("debroglie",    "quantum",         "Длина волны де Бройля",
            "\\lambda = \\frac{h}{mv}",
            "Волновые свойства частиц материи",
            "м",
            """{"λ":"длина волны","h":"6.626·10⁻³⁴","m":"масса","v":"скорость"}""",
            difficulty = 4),

        // === Ядерная физика ===
        FormulaEntity("radioactive_decay","nuclear",     "Закон радиоактивного распада",
            "N(t) = N_0 e^{-\\lambda t}",
            "Количество нераспавшихся ядер со временем",
            "ед.",
            """{"N_0":"начальное число ядер","λ":"постоянная распада","t":"время"}""",
            difficulty = 4),

        FormulaEntity("mass_defect",  "nuclear",         "Дефект масс и энергия связи",
            "E = \\Delta m \\cdot c^2",
            "Энергия связи атомного ядра",
            "Дж",
            """{"E":"энергия связи","Δm":"дефект масс","c":"3·10⁸ м/с"}""",
            difficulty = 5),

        // === Теория относительности ===
        FormulaEntity("emc2",         "relativity",      "Эквивалентность массы и энергии",
            "E = mc^2",
            "Полная энергия покоящегося тела",
            "Дж",
            """{"E":"энергия","m":"масса","c":"3·10⁸ м/с"}""",
            difficulty = 3)
    )

    // ─── Связи между формулами (граф) ─────────────────────────────────────────
    private fun buildLinks() = listOf(
        FormulaLinkEntity("f_newton_2",    "momentum",        "derives_from"),
        FormulaLinkEntity("kinematics_v",  "kinematics_s",    "related"),
        FormulaLinkEntity("kinematics_v",  "f_newton_2",      "related"),
        FormulaLinkEntity("energy_kinetic","momentum",         "related"),
        FormulaLinkEntity("energy_kinetic","energy_potential", "related"),
        FormulaLinkEntity("gravity",       "f_newton_2",      "used_in"),
        FormulaLinkEntity("ohm_law",       "power_electric",  "derives_from"),
        FormulaLinkEntity("coulomb",       "electric_field",  "derives_from"),
        FormulaLinkEntity("photoeffect",   "debroglie",       "related"),
        FormulaLinkEntity("mass_defect",   "emc2",            "derives_from"),
        FormulaLinkEntity("radioactive_decay","mass_defect",  "related"),
        FormulaLinkEntity("ideal_gas",     "first_law_thermo","related")
    )
}
