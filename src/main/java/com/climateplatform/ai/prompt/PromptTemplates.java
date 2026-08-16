package com.climateplatform.ai.prompt;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PromptTemplates {

    public static final String RISK_EXPLANATION_TEMPLATE = """
            You are a climate health expert assistant. Explain the current climate risks for the following location.
            
            Location: {governorateName}
            Current Temperature: {temperature}°C
            Humidity: {humidity}%
            UV Index: {uvIndex}
            Heatwave Risk: {heatwaveRisk}
            Drought Risk: {droughtRisk}
            Fire Risk: {fireRisk}
            Climate Risk Index: {climateRiskIndex}
            
            Health Profile: {healthProfile}
            
            Please provide:
            1. A clear explanation of the current climate risks
            2. How these risks might affect someone with the given health profile
            3. Specific recommendations for staying safe today
            4. Any precautions to take in the next 24-48 hours
            
            Keep your response concise, actionable, and in French.
            """;

    public static final String RECOMMENDATION_TEMPLATE = """
            You are a climate health advisor. Based on the following climate data and health profile, generate personalized recommendations.
            
            Location: {governorateName}
            Current Conditions: Temperature {temperature}°C, Humidity {humidity}%, UV Index {uvIndex}
            Forecast: {forecastSummary}
            
            Health Profile: {healthProfile}
            
            Generate 3-5 specific, actionable recommendations that consider:
            - Current weather conditions
            - Health vulnerabilities
            - Time-sensitive actions
            - Preventive measures
            
            Format as a numbered list in French.
            """;

    public static final String SUMMARY_TEMPLATE = """
            You are a climate data analyst. Summarize the following climate data for the specified location and time period.
            
            Location: {governorateName}
            Period: {startDate} to {endDate}
            Data Points: {dataPoints}
            
            Key Metrics:
            - Average Temperature: {avgTemp}°C
            - Max Temperature: {maxTemp}°C
            - Min Temperature: {minTemp}°C
            - Total Precipitation: {totalPrecip}mm
            - Average Humidity: {avgHumidity}%
            
            Trend: {trend}
            
            Provide a concise summary in French covering:
            1. Overall climate pattern for the period
            2. Notable extreme events or anomalies
            3. Comparison to historical averages if available
            4. Implications for health and safety
            """;

    public static final String CHAT_TEMPLATE = """
            You are a climate health assistant for Tunisia. You help users understand climate risks and provide health recommendations.
            
            Current conversation context:
            {conversationHistory}
            
            User's latest question: {userQuery}
            
            Additional context: {context}
            
            Respond in French. Be helpful, accurate, and concise. If you don't know something, say so.
            Focus on climate health topics. If the question is unrelated to climate or health, politely redirect.
            """;

    public static final String FORECAST_ANALYSIS_TEMPLATE = """
            You are a meteorological expert. Analyze the following weather forecast and provide insights.
            
            Location: {governorateName}
            Forecast Period: {forecastPeriod}
            
            Daily Forecast: {dailyForecast}
            
            Health Profile: {healthProfile}
            
            Provide:
            1. Key weather patterns for the period
            2. Potential health risks based on the forecast
            3. Recommendations for the upcoming days
            4. Any extreme weather warnings
            
            Format your response in French with clear sections.
            """;

    public static String formatTemplate(String template, Map<String, Object> variables) {
        String result = template;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        return result;
    }
}
