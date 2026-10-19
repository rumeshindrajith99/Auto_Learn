package com.autolearn.ar.data

/**
 * Complete educational content for the Theory Library.
 *
 * Each [TaskTheory] entry holds everything a single
 * theory-detail screen needs to render a full learning
 * page — overview text, learning objectives, safety
 * warnings, and sample chatbot question-answer pairs.
 */

import androidx.annotation.DrawableRes
import com.autolearn.ar.R

data class TaskTheory(
    val componentId: String,
    val title: String,
    val subtitle: String,
    val overview: String,
    val whyItMatters: String,
    val learningObjectives: List<String>,
    val safetyWarnings: List<String>,
    val procedureSteps: List<String>,
    val proTips: List<String>,
    val sampleQA: List<Pair<String, String>>,
    val hasPracticalPipeline: Boolean,
    @DrawableRes val imageResId: Int? = null
)


val theoryContentMap: Map<String, TaskTheory> = listOf(


    // ==================================================
    // 1.  AIR FILTER INSPECTION & REPLACEMENT
    // ==================================================

    TaskTheory(
        componentId = "air_filter",
        title = "Air Filter Inspection & Replacement",
        subtitle = "Engine Intake Filtration System",
        overview = "A dirty or clogged air filter restricts airflow into the engine, " +
                "which can reduce fuel efficiency and engine performance over time. " +
                "Checking and replacing it is one of the simplest and most common " +
                "pieces of routine maintenance a student will perform.",
        whyItMatters = "The air filter prevents dust, dirt, and debris from entering the " +
                "engine's combustion chambers. A blocked filter forces the engine to work " +
                "harder, increasing fuel consumption and potentially causing long-term wear " +
                "on internal components.",
        learningObjectives = listOf(
            "Locate the air filter housing under the hood and recognize its typical appearance.",
            "Open the housing (clips or screws) without damaging the seal.",
            "Visually inspect a filter for dirt, dust build-up, or tears — holding it up to light is a standard field check.",
            "Fit a new filter the correct way round and confirm the housing is fully closed and seated."
        ),
        safetyWarnings = listOf(
            "Engine should be off and cool before opening the housing.",
            "Avoid dropping debris into the open air intake while the filter is out.",
            "Handle the dirty filter carefully to avoid inhaling accumulated dust."
        ),
        procedureSteps = listOf(
            "Switch off the engine and allow it to cool.",
            "Open the engine compartment and locate the air filter housing.",
            "Release the housing clips or unscrew the fasteners carefully.",
            "Lift the housing cover and remove the air filter element.",
            "Inspect the filter — hold it up to a light source to check for blockage or tears.",
            "If the filter is excessively dirty or damaged, replace it with a new one.",
            "Place the new or cleaned filter into the housing, ensuring correct orientation.",
            "Close the housing and secure all clips or screws firmly."
        ),
        proTips = listOf(
            "A clean filter lets light pass through easily; a blocked one will appear dark and opaque.",
            "Always check the gasket/seal around the housing — a poor seal lets unfiltered air into the engine.",
            "Most manufacturers recommend checking the air filter every 15,000–30,000 km."
        ),
        sampleQA = listOf(
            "How do I know if my air filter needs replacing rather than just cleaning?" to
                    "If the filter is a paper/cellulose type, it generally cannot be cleaned effectively " +
                    "and should be replaced when visibly dirty. Foam or cotton-gauze filters can sometimes " +
                    "be washed and re-oiled, but paper filters should be swapped out.",
            "What happens to the engine if I skip this for too long?" to
                    "A severely clogged filter starves the engine of air, causing a richer fuel mixture, " +
                    "reduced power output, increased fuel consumption, and in extreme cases, engine misfires " +
                    "or stalling. It can also allow unfiltered particles into the engine if the filter tears.",
            "Can I drive without an air filter temporarily?" to
                    "Driving without an air filter is strongly discouraged. Even a short drive exposes the " +
                    "engine internals to dust and debris, which can cause accelerated wear on pistons, " +
                    "cylinders, and valves."
        ),
        hasPracticalPipeline = true,
        imageResId = R.drawable.img_air_filter
    ),


    // ==================================================
    // 2.  SPARK PLUG REMOVAL & GAP INSPECTION
    // ==================================================

    TaskTheory(
        componentId = "spark_plug",
        title = "Spark Plug Removal & Gap Inspection",
        subtitle = "Engine Ignition System Component",
        overview = "Spark plugs ignite the fuel-air mixture in each cylinder. A worn " +
                "electrode or an incorrect gap is a common cause of misfires, poor fuel " +
                "economy, and hard starting, which makes this a high-value diagnostic skill.",
        whyItMatters = "The spark plug is the final link in the ignition chain — if it " +
                "fails to produce a strong, correctly-timed spark, the cylinder will not " +
                "fire properly. Learning to read a spark plug's condition is also a basic " +
                "but powerful diagnostic tool.",
        learningObjectives = listOf(
            "Safely access a spark plug by removing the ignition coil or plug wire first.",
            "Use a spark plug socket correctly to avoid damaging the porcelain insulator or cylinder head threads.",
            "Read a gap gauge and compare the measured gap to the specified value.",
            "Interpret the electrode's condition — normal wear vs. fouling vs. excessive wear — as a diagnostic cue.",
            "Reinstall a plug by hand first to avoid cross-threading, then tighten to correct torque."
        ),
        safetyWarnings = listOf(
            "Engine must be off and fully cooled — spark plugs and surrounding metal can be very hot after running.",
            "Never force a plug that doesn't turn freely by hand at first — cross-threading damages the cylinder head.",
            "Keep the socket seated straight to avoid cracking the porcelain insulator.",
            "Use the correct spark plug socket size for your vehicle — an incorrect fit can slip and damage the plug."
        ),
        procedureSteps = listOf(
            "Switch off the engine and allow it to cool completely.",
            "Locate the spark plug area on the engine.",
            "Carefully disconnect the spark plug lead or ignition coil connector.",
            "Use compressed air or a brush to clean debris around the plug before removal.",
            "Attach the correct spark plug socket and loosen the plug by turning counter-clockwise.",
            "Remove the old spark plug and inspect its condition.",
            "Check the new spark plug's gap using a feeler gauge — adjust if necessary.",
            "Thread the new plug in by hand first (at least 2–3 full turns) to prevent cross-threading.",
            "Tighten to the manufacturer's specified torque.",
            "Reconnect the ignition coil or plug lead securely."
        ),
        proTips = listOf(
            "A light tan or grey deposit on the electrode indicates normal combustion.",
            "Black, sooty deposits suggest a rich fuel mixture or oil burning.",
            "A white, blistered electrode may indicate overheating or incorrect heat range.",
            "Always replace spark plugs as a complete set for consistent performance."
        ),
        sampleQA = listOf(
            "What does it mean if the plug looks black and sooty?" to
                    "Black, dry soot (carbon fouling) usually indicates a rich fuel-air mixture, a clogged " +
                    "air filter, or excessive idling. Wet, oily black deposits suggest oil is leaking into " +
                    "the combustion chamber, which may point to worn valve seals or piston rings.",
            "Why does the gap matter, and what happens if it's wrong?" to
                    "The gap determines how far the spark must jump. Too wide a gap can cause misfires " +
                    "because the ignition coil may not generate enough voltage. Too narrow a gap produces " +
                    "a weak spark that may not fully ignite the mixture, leading to poor performance and " +
                    "increased emissions.",
            "How often should spark plugs be replaced?" to
                    "Standard copper-core plugs typically last 30,000–50,000 km. Iridium or platinum plugs " +
                    "can last 80,000–160,000 km. Always follow the manufacturer's recommendation for your " +
                    "specific vehicle."
        ),
        hasPracticalPipeline = false,
        imageResId = R.drawable.img_spark_plug
    ),


    // ==================================================
    // 3.  12V BATTERY TERMINAL MAINTENANCE & VOLTAGE CHECK
    // ==================================================

    TaskTheory(
        componentId = "battery_terminal",
        title = "Battery Terminal Maintenance & Voltage Check",
        subtitle = "Vehicle Electrical System",
        overview = "Corroded or loose battery terminals are one of the most common causes " +
                "of a car failing to start. This task also introduces a student to basic " +
                "electrical safety and multimeter use.",
        whyItMatters = "The battery is the heart of the vehicle's electrical system. A poor " +
                "terminal connection can prevent starting, cause dim lights, trigger warning " +
                "lights, and even damage sensitive electronic modules.",
        learningObjectives = listOf(
            "Identify which terminal is positive (+) and which is negative (−), and why disconnect/reconnect order matters.",
            "Recognize corrosion on a terminal and clean it safely using a wire brush or baking-soda-and-water solution.",
            "Confirm that a reconnected terminal is tight and free of corrosion.",
            "Use a multimeter to read battery voltage — healthy resting voltage is around 12.6V; running/charging voltage is 13.7–14.7V."
        ),
        safetyWarnings = listOf(
            "Always disconnect the negative terminal first and reconnect it last to avoid short circuits.",
            "Never let a tool bridge both terminals at once — this creates a dangerous short circuit.",
            "No open flame or sparks near the battery — batteries can vent flammable hydrogen gas.",
            "Wear eye protection when cleaning corrosion; battery acid residue can irritate skin and eyes.",
            "If battery acid contacts skin, flush immediately with plenty of water."
        ),
        procedureSteps = listOf(
            "Switch off the engine and remove the ignition key.",
            "Wear appropriate safety protection (gloves and eye protection).",
            "Locate the vehicle battery and identify the positive (+) and negative (−) terminals.",
            "Disconnect the negative (−) battery terminal first using the correct wrench.",
            "Disconnect the positive (+) battery terminal.",
            "Inspect both terminals and cable ends for corrosion (white/green crusty deposits).",
            "Clean corroded terminals with a wire brush or baking soda solution.",
            "Reconnect the positive (+) terminal first and tighten securely.",
            "Reconnect the negative (−) terminal and tighten securely.",
            "Set your multimeter to DC voltage (20V range) and touch probes to the correct terminals.",
            "Read the voltage — a healthy battery at rest should read approximately 12.4–12.7V."
        ),
        proTips = listOf(
            "Applying a thin layer of petroleum jelly or terminal protector spray after cleaning helps prevent future corrosion.",
            "A resting voltage below 12.2V suggests the battery may need charging or replacement.",
            "If the voltage drops below 9.6V during cranking, the battery is likely failing.",
            "Always keep battery terminals tight — even slight looseness causes resistance and heat."
        ),
        sampleQA = listOf(
            "Why do I disconnect the negative terminal first?" to
                    "The negative terminal is connected to the vehicle's chassis (ground). Disconnecting it " +
                    "first eliminates the risk of accidentally shorting the positive terminal to the chassis " +
                    "if your wrench touches any metal body part while working. Reversing this order means the " +
                    "entire chassis is live, and any accidental contact creates a short circuit.",
            "What voltage reading means my battery is weak or needs replacing?" to
                    "A fully charged 12V battery at rest should read 12.6V or above. Between 12.0V and 12.4V, " +
                    "it is partially discharged and should be recharged. Below 11.8V at rest, the battery is " +
                    "deeply discharged and may not recover. If it cannot hold 12.4V after a full charge, " +
                    "replacement is recommended.",
            "What causes battery terminal corrosion?" to
                    "Corrosion is caused by hydrogen gas released during charging reacting with the metal " +
                    "terminals, as well as acid fumes escaping through tiny gaps. Heat and age accelerate " +
                    "the process. Keeping terminals clean and protected slows corrosion significantly."
        ),
        hasPracticalPipeline = false,
        imageResId = R.drawable.img_battery_terminal
    ),


    // ==================================================
    // 4.  ENGINE OIL LEVEL VERIFICATION (DIPSTICK CHECK)
    // ==================================================

    TaskTheory(
        componentId = "engine_oil_dipstick",
        title = "Engine Oil Level Verification",
        subtitle = "Dipstick Check Procedure",
        overview = "Running an engine with too little oil is one of the fastest ways to " +
                "cause serious, expensive damage. A correct dipstick check is a " +
                "foundational habit for any student to build early.",
        whyItMatters = "Engine oil lubricates moving parts, reduces friction, cools components, " +
                "and carries away contaminants. Without adequate oil, metal-on-metal contact " +
                "causes rapid wear, overheating, and potentially catastrophic engine failure — " +
                "an outcome that is entirely preventable with a 60-second check.",
        learningObjectives = listOf(
            "Locate the dipstick — usually identified by a brightly colored handle (yellow or orange).",
            "Follow the correct procedure: engine off, vehicle on level ground, checked cold or a few minutes after shutdown.",
            "Pull, wipe clean, fully reinsert, and pull again for an accurate reading.",
            "Read the level against the min/max markings.",
            "Notice oil that looks unusually dark, gritty, or milky — each indicates a different problem."
        ),
        safetyWarnings = listOf(
            "Let the engine cool enough that nearby components won't burn your hand or arm.",
            "Dispose of the wiping rag or paper towel appropriately — do not leave it in the engine bay.",
            "Avoid prolonged skin contact with used engine oil — it contains contaminants. Wash hands after handling."
        ),
        procedureSteps = listOf(
            "Park the vehicle on level ground to ensure an accurate reading.",
            "Switch off the engine.",
            "Allow the engine oil to settle for a few minutes.",
            "Locate the engine oil dipstick (brightly colored handle).",
            "Pull the dipstick out completely.",
            "Wipe the dipstick clean with a lint-free cloth or paper towel.",
            "Insert the dipstick fully back into its tube.",
            "Pull it out again smoothly and check the oil level.",
            "The oil level should be between the minimum and maximum markings.",
            "Also check the oil's color and consistency — it should be amber to dark brown and smooth."
        ),
        proTips = listOf(
            "Always wipe and re-check — the first pull is unreliable because oil splashes during driving.",
            "Amber or honey-colored oil is fresh; dark brown or black oil is due for a change.",
            "Milky or frothy oil may indicate coolant contamination — a serious issue requiring immediate attention.",
            "Never overfill — too much oil can cause foaming, which reduces lubrication effectiveness."
        ),
        sampleQA = listOf(
            "Why do I need to wipe the dipstick and check it twice?" to
                    "Oil splashes around inside the engine while driving, coating the dipstick unevenly. " +
                    "The first reading is unreliable. Wiping it clean and reinserting gives you the actual " +
                    "settled oil level in the sump, which is the accurate measurement.",
            "What does it mean if the oil looks milky or gritty?" to
                    "Milky or frothy oil (often a creamy beige color) usually indicates coolant is leaking " +
                    "into the oil, possibly from a blown head gasket. Gritty or metalite-flecked oil suggests " +
                    "internal wear producing metal particles. Both conditions require prompt professional diagnosis.",
            "How often should I check the oil level?" to
                    "Most manufacturers recommend checking oil at least once a month or before long trips. " +
                    "Older or high-mileage engines may consume oil between changes, making regular checks " +
                    "even more important."
        ),
        hasPracticalPipeline = false,
        imageResId = R.drawable.img_engine_oil_dipstick
    ),


    // ==================================================
    // 5.  ENGINE COOLANT RESERVOIR LEVEL INSPECTION
    // ==================================================

    TaskTheory(
        componentId = "coolant_reservoir",
        title = "Engine Coolant Reservoir Level Inspection",
        subtitle = "Engine Cooling System",
        overview = "Low coolant is a leading cause of engine overheating. This task also " +
                "teaches an important safety rule — never open a hot cooling system — " +
                "that protects students from a genuinely dangerous mistake.",
        whyItMatters = "The cooling system maintains the engine at its optimal operating temperature. " +
                "Without sufficient coolant, the engine overheats rapidly, which can warp the " +
                "cylinder head, blow the head gasket, and cause thousands of dollars in damage — " +
                "or total engine failure.",
        learningObjectives = listOf(
            "Identify the coolant reservoir — usually a translucent plastic tank with min/max markings, separate from the radiator cap.",
            "Check the level correctly against the min/max lines, and understand why the level should be checked cold.",
            "Top up with the correct type of coolant if the level is low, without overfilling.",
            "Understand why the coolant reservoir, not the radiator cap, is the normal day-to-day check point."
        ),
        safetyWarnings = listOf(
            "NEVER open a radiator cap or the cooling system while the engine is warm or hot — it is pressurized and can cause severe burns.",
            "Coolant (antifreeze) is toxic — handle and store it away from skin contact, and keep it away from children and pets.",
            "Only top up with the coolant type specified for that vehicle — mixing types can cause corrosion and blockages.",
            "Clean up any coolant spills immediately — it has a sweet taste that can attract animals."
        ),
        procedureSteps = listOf(
            "Ensure the engine is completely cool before beginning.",
            "Open the engine compartment and locate the coolant reservoir (translucent plastic tank).",
            "Check the coolant level against the MIN and MAX markings on the side of the reservoir.",
            "If the level is below the MIN line, the system needs topping up.",
            "Identify the correct coolant type for the vehicle (check the owner's manual).",
            "Slowly add coolant to the reservoir until it reaches the MAX line — do not overfill.",
            "Replace the reservoir cap securely.",
            "If the level drops repeatedly, there may be a leak in the cooling system that requires professional inspection."
        ),
        proTips = listOf(
            "Coolant expands when hot — always check the level when the engine is cold for an accurate reading.",
            "Most modern coolants are pre-mixed (50/50 coolant and water). Using pure concentrate can reduce cooling efficiency.",
            "A persistent sweet smell from the engine bay may indicate a coolant leak.",
            "Never use plain water as a long-term coolant replacement — it lacks the corrosion inhibitors and freeze protection of proper coolant."
        ),
        sampleQA = listOf(
            "Why can't I just check the radiator cap directly?" to
                    "The radiator cap seals a pressurized system. Opening it when hot releases superheated " +
                    "coolant and steam, which can cause severe burns. The reservoir is a separate, non-pressurized " +
                    "(or low-pressure) overflow tank designed for safe visual checking without opening the " +
                    "pressurized circuit.",
            "Is it dangerous if the coolant is a little low?" to
                    "A small drop is normal over time due to evaporation. However, running consistently low " +
                    "reduces the system's ability to absorb heat, raising engine temperature. Significantly low " +
                    "coolant can lead to overheating within minutes under load. If you're topping up frequently, " +
                    "investigate for leaks.",
            "What happens if I mix different coolant types?" to
                    "Mixing incompatible coolant types (e.g., silicate-based green with organic-acid-based orange) " +
                    "can cause a chemical reaction that produces gel or sludge, blocking cooling passages and " +
                    "accelerating corrosion. Always match the type specified in the owner's manual."
        ),
        hasPracticalPipeline = false,
        imageResId = R.drawable.img_coolant_reservoir
    )

).associateBy { it.componentId }
