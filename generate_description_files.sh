#!/bin/bash

# Script to generate all description JSON files for experienceType validation tests

cd "$(dirname "$0")"
CATEGORIES_DIR="resources/data/categories"

# Activity - Adventure Sports
cat > "$CATEGORIES_DIR/bungee_jumping_goa_description.json" << 'EOF'
{
  "experienceType": "Activity",
  "experience": {
    "title": "Bungee Jumping Adventure in Goa",
    "description": "Take a leap of faith with an exhilarating bungee jumping experience in Goa. Jump from a 200-foot platform overlooking lush valleys and experience the ultimate rush of free-falling before the bungee cord rebounds you back up. Professional safety equipment and certified instructors ensure a safe yet thrilling adventure for adrenaline seekers.",
    "location": "Goa"
  },
  "requestParams": {
    "experienceTitle": "Bungee Jumping Adventure in Goa",
    "experienceDescription": "Take a leap of faith with an exhilarating bungee jumping experience in Goa. Jump from a 200-foot platform overlooking lush valleys and experience the ultimate rush of free-falling before the bungee cord rebounds you back up. Professional safety equipment and certified instructors ensure a safe yet thrilling adventure for adrenaline seekers.",
    "experienceLocation": "Goa"
  }
}
EOF

cat > "$CATEGORIES_DIR/whitewater_rafting_manali_description.json" << 'EOF'
{
  "experienceType": "Activity",
  "experience": {
    "title": "Whitewater Rafting in Manali",
    "description": "Navigate the rushing rapids of the Beas River on an exciting whitewater rafting expedition in Manali. Experience Grade II to IV rapids as you paddle through stunning Himalayan landscapes, with snow-capped peaks and pine forests as your backdrop. Professional guides provide safety briefing and equipment for this thrilling water adventure.",
    "location": "Manali, Himachal Pradesh"
  },
  "requestParams": {
    "experienceTitle": "Whitewater Rafting in Manali",
    "experienceDescription": "Navigate the rushing rapids of the Beas River on an exciting whitewater rafting expedition in Manali. Experience Grade II to IV rapids as you paddle through stunning Himalayan landscapes, with snow-capped peaks and pine forests as your backdrop. Professional guides provide safety briefing and equipment for this thrilling water adventure.",
    "experienceLocation": "Manali, Himachal Pradesh"
  }
}
EOF

# Activity - Creative Workshops
cat > "$CATEGORIES_DIR/cooking_classes_kerala_description.json" << 'EOF'
{
  "experienceType": "Activity",
  "experience": {
    "title": "Traditional Cooking Classes in Kerala",
    "description": "Learn to prepare authentic Kerala cuisine in a traditional cooking class led by local chefs. Master the art of making coconut-based curries, fish molee, appam, and puttu while discovering the secrets of Kerala spices. This hands-on workshop includes a visit to spice gardens, market tour, and a delicious meal of your prepared dishes.",
    "location": "Kerala"
  },
  "requestParams": {
    "experienceTitle": "Traditional Cooking Classes in Kerala",
    "experienceDescription": "Learn to prepare authentic Kerala cuisine in a traditional cooking class led by local chefs. Master the art of making coconut-based curries, fish molee, appam, and puttu while discovering the secrets of Kerala spices. This hands-on workshop includes a visit to spice gardens, market tour, and a delicious meal of your prepared dishes.",
    "experienceLocation": "Kerala"
  }
}
EOF

cat > "$CATEGORIES_DIR/dance_workshops_bangalore_description.json" << 'EOF'
{
  "experienceType": "Activity",
  "experience": {
    "title": "Classical Dance Workshops in Bangalore",
    "description": "Immerse yourself in the graceful world of Indian classical dance with workshops in Bharatanatyam, Kathak, or Kuchipudi in Bangalore. Learn basic postures, hand gestures (mudras), facial expressions, and rhythmic movements under the guidance of accomplished dance masters. Perfect for beginners and those seeking cultural immersion.",
    "location": "Bangalore, Karnataka"
  },
  "requestParams": {
    "experienceTitle": "Classical Dance Workshops in Bangalore",
    "experienceDescription": "Immerse yourself in the graceful world of Indian classical dance with workshops in Bharatanatyam, Kathak, or Kuchipudi in Bangalore. Learn basic postures, hand gestures (mudras), facial expressions, and rhythmic movements under the guidance of accomplished dance masters. Perfect for beginners and those seeking cultural immersion.",
    "experienceLocation": "Bangalore, Karnataka"
  }
}
EOF

# Activity - Health & Wellness
cat > "$CATEGORIES_DIR/ayurveda_treatments_kerala_description.json" << 'EOF'
{
  "experienceType": "Activity",
  "experience": {
    "title": "Authentic Ayurveda Treatments in Kerala",
    "description": "Experience traditional Ayurvedic healing with personalized treatments in Kerala's renowned wellness centers. Enjoy therapeutic massages, herbal steam baths, Panchakarma detoxification, and consultations with Ayurvedic doctors. Treatments use ancient herbs and oils to balance your doshas and promote holistic well-being.",
    "location": "Kerala"
  },
  "requestParams": {
    "experienceTitle": "Authentic Ayurveda Treatments in Kerala",
    "experienceDescription": "Experience traditional Ayurvedic healing with personalized treatments in Kerala's renowned wellness centers. Enjoy therapeutic massages, herbal steam baths, Panchakarma detoxification, and consultations with Ayurvedic doctors. Treatments use ancient herbs and oils to balance your doshas and promote holistic well-being.",
    "experienceLocation": "Kerala"
  }
}
EOF

cat > "$CATEGORIES_DIR/meditation_centers_dharamshala_description.json" << 'EOF'
{
  "experienceType": "Activity",
  "experience": {
    "title": "Meditation Centers in Dharamshala",
    "description": "Find inner peace at meditation centers in Dharamshala, the spiritual home of the Dalai Lama. Participate in guided meditation sessions, mindfulness practices, and Buddhist teachings in serene mountain settings. Learn various meditation techniques including Vipassana, Tibetan Buddhist meditation, and walking meditation among peaceful monasteries.",
    "location": "Dharamshala, Himachal Pradesh"
  },
  "requestParams": {
    "experienceTitle": "Meditation Centers in Dharamshala",
    "experienceDescription": "Find inner peace at meditation centers in Dharamshala, the spiritual home of the Dalai Lama. Participate in guided meditation sessions, mindfulness practices, and Buddhist teachings in serene mountain settings. Learn various meditation techniques including Vipassana, Tibetan Buddhist meditation, and walking meditation among peaceful monasteries.",
    "experienceLocation": "Dharamshala, Himachal Pradesh"
  }
}
EOF

echo "Generated Activity category description files..."

# Events - Festivals
cat > "$CATEGORIES_DIR/holi_festival_mathura_description.json" << 'EOF'
{
  "experienceType": "Events",
  "experience": {
    "title": "Holi Festival Celebration in Mathura",
    "description": "Celebrate the vibrant Holi festival in Mathura, the birthplace of Lord Krishna. Join thousands of devotees in the colorful festivities, throw gulal (colored powder), dance to traditional music, and witness the famous Lathmar Holi. Experience temple celebrations, cultural performances, and the joyous spirit of this ancient festival of colors.",
    "location": "Mathura, Uttar Pradesh"
  },
  "requestParams": {
    "experienceTitle": "Holi Festival Celebration in Mathura",
    "experienceDescription": "Celebrate the vibrant Holi festival in Mathura, the birthplace of Lord Krishna. Join thousands of devotees in the colorful festivities, throw gulal (colored powder), dance to traditional music, and witness the famous Lathmar Holi. Experience temple celebrations, cultural performances, and the joyous spirit of this ancient festival of colors.",
    "experienceLocation": "Mathura, Uttar Pradesh"
  }
}
EOF

cat > "$CATEGORIES_DIR/diwali_celebrations_delhi_description.json" << 'EOF'
{
  "experienceType": "Events",
  "experience": {
    "title": "Diwali Celebrations in Delhi",
    "description": "Experience the grandeur of Diwali, the Festival of Lights, in Delhi with spectacular fireworks displays, beautifully illuminated markets, and traditional celebrations. Visit decorated temples, participate in rangoli making, enjoy festive sweets, and witness the magical transformation of the city with millions of diyas and lights.",
    "location": "Delhi"
  },
  "requestParams": {
    "experienceTitle": "Diwali Celebrations in Delhi",
    "experienceDescription": "Experience the grandeur of Diwali, the Festival of Lights, in Delhi with spectacular fireworks displays, beautifully illuminated markets, and traditional celebrations. Visit decorated temples, participate in rangoli making, enjoy festive sweets, and witness the magical transformation of the city with millions of diyas and lights.",
    "experienceLocation": "Delhi"
  }
}
EOF

echo "Generated partial Events category description files..."

echo "✅ Generated sample description JSON files for experienceType validation tests!"
echo "📝 Files created in: $CATEGORIES_DIR"
echo "🔧 Continue running this script or create remaining files as needed..."
