class AppConstants {
  static const String appName = 'AutoLearn AR';
  static const String vehicleName = 'Maruti Suzuki Alto 800L';

  static const List<Map<String, dynamic>> tasks = [
    {
      'id': 'task_001',
      'title': 'Air Filter Inspection',
      'subtitle': 'Inspection & Replacement',
      'component': 'air_filter',
      'difficulty': 'Easy',
      'duration': '10 min',
      'color': 0xFF1565C0,
    },
    {
      'id': 'task_002',
      'title': 'Spark Plug',
      'subtitle': 'Removal & Gap Inspection',
      'component': 'spark_plug',
      'difficulty': 'Medium',
      'duration': '15 min',
      'color': 0xFFFF6F00,
    },
    {
      'id': 'task_003',
      'title': '12V Battery',
      'subtitle': 'Terminal Maintenance',
      'component': 'battery',
      'difficulty': 'Medium',
      'duration': '20 min',
      'color': 0xFFD32F2F,
    },
    {
      'id': 'task_004',
      'title': 'Engine Oil',
      'subtitle': 'Dipstick Level Check',
      'component': 'engine_oil',
      'difficulty': 'Easy',
      'duration': '5 min',
      'color': 0xFF2E7D32,
    },
    {
      'id': 'task_005',
      'title': 'Coolant Reservoir',
      'subtitle': 'Level Inspection',
      'component': 'coolant',
      'difficulty': 'Easy',
      'duration': '5 min',
      'color': 0xFF00897B,
    },
  ];

  static const Map<String, List<String>> taskSteps = {
    'air_filter': [
      'Locate the air filter box near the engine intake',
      'Unclip the 4 clips around the air filter housing',
      'Carefully lift the housing cover',
      'Remove the air filter element',
      'Inspect filter — hold up to light to check blockage',
      'If dirty, tap gently to remove loose dust',
      'Insert new/cleaned filter back into housing',
      'Close the housing and re-clip all 4 clips',
    ],
    'spark_plug': [
      'Locate the spark plug wire on top of the engine',
      'Gently twist and pull the plug wire to remove',
      'Use a spark plug socket wrench (16mm)',
      'Turn counter-clockwise to loosen the spark plug',
      'Remove the spark plug fully',
      'Use a feeler gauge to measure the gap (0.7–0.8mm)',
      'If gap is wrong, carefully bend the ground electrode',
      'Re-insert plug and tighten clockwise (finger-tight first)',
      'Reconnect the plug wire firmly until you hear a click',
    ],
    'battery': [
      'Identify the 12V battery location near the firewall',
      'Always disconnect NEGATIVE terminal first',
      'Use 10mm spanner to loosen the negative clamp bolt',
      'Remove and set aside the negative cable',
      'Now disconnect POSITIVE terminal the same way',
      'Inspect terminals for white or blue corrosion',
      'Clean terminals with wire brush and baking soda solution',
      'Reconnect POSITIVE first, then NEGATIVE',
      'Use a multimeter set to DC Volts to check voltage',
      'A healthy battery reads 12.4V to 12.7V',
    ],
    'engine_oil': [
      'Park on level ground and turn off the engine',
      'Wait 5 minutes for oil to settle',
      'Open the hood and locate the dipstick (yellow handle)',
      'Pull out the dipstick and wipe clean with a rag',
      'Re-insert dipstick fully, then pull out again',
      'Check where the oil mark is between MIN and MAX',
      'Oil should be between MIN and MAX markings',
      'If below MIN, add the correct engine oil grade (5W-30)',
    ],
    'coolant': [
      'Ensure engine is completely COLD before opening anything',
      'Locate the translucent coolant reservoir (white bottle)',
      'Check the level against MIN and MAX markings on the side',
      'Level should be between MIN and MAX',
      'If low, open the reservoir cap slowly using a cloth',
      'Top up with 50/50 premixed coolant only',
      'Close the cap firmly and check for any leaks',
    ],
  };

  static const Map<String, List<String>> safetyWarnings = {
    'battery': [
      'High voltage risk — remove metal jewelry before starting',
      'Always disconnect NEGATIVE first to prevent short circuit',
      'Battery acid is corrosive — wear gloves and eye protection',
    ],
    'spark_plug': [
      'Engine must be COLD — hot plugs cause severe burns',
      'Do not overtighten — can crack the cylinder head',
    ],
    'coolant': [
      'NEVER open radiator cap on a hot engine — explosion risk',
      'Coolant is toxic — keep away from children and animals',
    ],
    'air_filter': [
      'Do not run engine without air filter fitted',
    ],
    'engine_oil': [
      'Hot oil causes serious burns — wait for engine to cool',
    ],
  };
}
