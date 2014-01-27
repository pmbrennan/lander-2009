proc writeGetterSetter {fname type mname} {
    puts "public $type Get$fname\(\)"
    puts "{"
    puts "    return $mname;"
    puts "}"
    puts ""
    puts "public void Set$fname ($type in_$fname)"
    puts "{"
    puts "    $mname = in_$fname;"
    puts "}"
    puts ""
}

# writeGetterSetter Fuel              double      m_fuel           
# writeGetterSetter FuelPercent       double      m_fuelpercent    
# writeGetterSetter DeltaV            double      m_deltav         
# writeGetterSetter ThrottlePercent   double      m_throttlepercent
# writeGetterSetter FlowRate          double      m_flowrate
# writeGetterSetter VSpeed            double      m_vspeed         
# writeGetterSetter HSpeed            double      m_hspeed         
# writeGetterSetter DatumAltitude     double      m_datum_altitude 
# writeGetterSetter RadarAltitude     double      m_radar_altitude 
# writeGetterSetter RadarOn           boolean     m_radar_on       
# writeGetterSetter TargetRange       double      m_target_range   
# writeGetterSetter Apolune           double      m_apolune        
# writeGetterSetter ApoluneOK         boolean     m_apolune_ok     
# writeGetterSetter Perilune          double      m_perilune       
# writeGetterSetter PeriluneOK        boolean     m_perilune_ok    
# writeGetterSetter TimeFactor        int         m_timefactor
# writeGetterSetter AutpilotOn        boolean     m_autopilot_on

proc addDataMember {fname type mname} {
    puts "\nprotected $type $mname;     // $fname\n"
    writeGetterSetter $fname $type $mname
}

#addDataMember   Time        double      m_time
#addDataMember    Pitch       double      m_pitch

addDataMember TargetSelected boolean m_target_set
addDataMember TargetLongitude double m_target_longitude
addDataMember TargetAltitude double m_target_altitude
addDataMember DistanceToTarget double m_distance_to_target


