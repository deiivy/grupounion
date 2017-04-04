SELECT t.team_name EQUIPO,
SUM(
IFNULL((SELECT SUM(CASE WHEN m.host_goals > m.guest_goals THEN 3 
WHEN m.host_goals = m.guest_goals THEN 1 ELSE 0 END) 'PUNTOS'
FROM matches m
WHERE m.host_team = t.team_id
GROUP BY m.host_team), 0)
+
IFNULL((SELECT SUM(CASE WHEN m.guest_goals > m.host_goals THEN 3 
WHEN m.guest_goals = m.host_goals THEN 1 ELSE 0 END) 'PUNTOS'
FROM matches m
WHERE m.guest_team = t.team_id
GROUP BY m.guest_team), 0)
) PUNTOS
FROM teams t 
GROUP BY t.team_id
ORDER BY PUNTOS DESC, t.team_id ASC;