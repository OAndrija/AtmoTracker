import { Box, IconButton, Typography, useTheme } from "@mui/material";
import { useContext } from "react";
import { ColorModeContext, tokens } from "../../theme";

const FilterButton = ({ icon, text }) => {
    const theme = useTheme();
    const colors = tokens(theme.palette.mode);
    const colorMode = useContext(ColorModeContext);

    return (
        <Box display="flex" alignItems="center" backgroundColor={colors.primary[400]} borderRadius="35px" sx={{ ml:1.5, pr:1, width: '50%', height: '33px', boxShadow: '0px 4px 10px rgba(0,0,0, 0.2)'}}>
            <IconButton sx={{ color: theme.palette.text.primary }}>
                {icon}
                <Typography fontSize={13} sx={{ml:0.5}}>
                    {text}
                </Typography>
            </IconButton>
        </Box>
    );
};

export default FilterButton;
