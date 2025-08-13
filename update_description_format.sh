#!/bin/bash

# Script to create properly formatted description files for the new ExperienceType validation tests

cd "$(dirname "$0")"
CATEGORIES_DIR="resources/data/categories"

# Create the remaining key test files in the correct format

# Activity - Health & Wellness
cat > "$CATEGORIES_DIR/yoga_retreats_goa_description.json" << 'EOF'
{
  "merchantId": "135699500050",
  "promptId": 1221,
  "promptTitle": "GenerateCategories",
  "requestParams": {
    "experienceCategory": "",
    "description": "Immerse yourself in a transformative yoga retreat on the serene beaches of Goa. This wellness experience combines daily yoga sessions, meditation practices, pranayama breathing techniques, and Ayurvedic treatments. Practice asanas during sunrise and sunset sessions while listening to the sound of ocean waves, and participate in spiritual workshops led by certified yoga masters.",
    "s_tag": "True"
  }
}
EOF

# Events - Festivals  
cat > "$CATEGORIES_DIR/holi_festival_mathura_description.json" << 'EOF'
{
  "merchantId": "135699500050",
  "promptId": 1221,
  "promptTitle": "GenerateCategories",
  "requestParams": {
    "experienceCategory": "",
    "description": "Celebrate the vibrant Holi festival in Mathura, the birthplace of Lord Krishna. Join thousands of devotees in the colorful festivities, throw gulal (colored powder), dance to traditional music, and witness the famous Lathmar Holi. Experience temple celebrations, cultural performances, and the joyous spirit of this ancient festival of colors.",
    "s_tag": "True"
  }
}
EOF

# Stay - Boutique Lodgings
cat > "$CATEGORIES_DIR/heritage_hotels_rajasthan_description.json" << 'EOF'
{
  "merchantId": "135699500050",
  "promptId": 1221,
  "promptTitle": "GenerateCategories",
  "requestParams": {
    "experienceCategory": "",
    "description": "Stay in magnificent heritage hotels that were once royal palaces and forts in Rajasthan. Experience regal hospitality with opulent suites, courtyards with fountains, traditional Rajasthani architecture, and world-class amenities. Enjoy cultural performances, royal dining experiences, and personalized service that reflects the grandeur of India's royal heritage.",
    "s_tag": "True"
  }
}
EOF

# Tours - Cultural Tours
cat > "$CATEGORIES_DIR/heritage_walks_delhi_description.json" << 'EOF'
{
  "merchantId": "135699500050",
  "promptId": 1221,
  "promptTitle": "GenerateCategories",
  "requestParams": {
    "experienceCategory": "",
    "description": "Explore Delhi's rich cultural heritage through guided walking tours of historic neighborhoods like Old Delhi, Chandni Chowk, and Mehrauli. Discover ancient monuments, traditional bazaars, Mughal architecture, colonial buildings, and hidden gems while learning about the city's fascinating history from expert local guides.",
    "s_tag": "True"
  }
}
EOF

# Attraction - Historical Sites
cat > "$CATEGORIES_DIR/ancient_temples_tamil_nadu_description.json" << 'EOF'
{
  "merchantId": "135699500050",
  "promptId": 1221,
  "promptTitle": "GenerateCategories",
  "requestParams": {
    "experienceCategory": "",
    "description": "Visit the magnificent ancient temples of Tamil Nadu, including the Brihadeeswarar Temple in Thanjavur, Meenakshi Temple in Madurai, and shore temples of Mamallapuram. Marvel at intricate Dravidian architecture, stone carvings, towering gopurams, and learn about centuries-old religious traditions and cultural heritage.",
    "s_tag": "True"
  }
}
EOF

# Rentals - Vehicle Rentals
cat > "$CATEGORIES_DIR/motorbike_rentals_goa_description.json" << 'EOF'
{
  "merchantId": "135699500050",
  "promptId": 1221,
  "promptTitle": "GenerateCategories",
  "requestParams": {
    "experienceCategory": "",
    "description": "Rent high-quality motorbikes and scooters to explore the scenic beauty of Goa at your own pace. Choose from a fleet of well-maintained bikes including Royal Enfield classics, sporty motorcycles, and comfortable scooters. Perfect for beach hopping, visiting spice plantations, and discovering hidden gems along coastal roads.",
    "s_tag": "True"
  }
}
EOF

echo "✅ Created properly formatted description files for ExperienceType validation tests!"
